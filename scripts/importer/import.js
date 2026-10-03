import fs from 'fs';
import path from 'path';
import os from 'os';
import { execFile } from 'child_process';
import { fileURLToPath } from 'url';
import inquirer from 'inquirer';
import cliProgress from 'cli-progress';
import chalk from 'chalk';
import axios from 'axios';
import AdmZip from 'adm-zip';
import { XMLParser } from 'fast-xml-parser';
import { glob } from 'glob';
import dotenv from 'dotenv';

dotenv.config();

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const API_BASE_URL = process.env.API_BASE_URL || 'http://localhost:8083/api';
const API_USERNAME = process.env.API_USERNAME || 'admin';
const API_PASSWORD = process.env.API_PASSWORD || 'admin';
const SERVER_URL = API_BASE_URL.replace(/\/api\/?$/, '');

const xmlParser = new XMLParser({
  ignoreAttributes: false,
  attributeNamePrefix: '@_',
  allowBooleanAttributes: true,
  parseTagValue: true,
  trimValues: true,
  textNodeName: '#text',
});

// Extensões suportadas
const MANGA_EXTENSIONS = ['.cbz', '.cbr', '.zip', '.rar', '.7z'];
const BOOK_EXTENSIONS = ['.epub', '.pdf', '.mobi', '.azw3'];

function formatDateToIso(date) {
  return date.toISOString().split('.')[0];
}

// Localiza o executável do RAR (no diretório local, pasta bin ou no PATH)
function getRarExecutable() {
  const localRar = path.join(__dirname, 'rar.exe');
  if (fs.existsSync(localRar)) return localRar;

  const binRar = path.join(__dirname, 'bin', 'rar.exe');
  if (fs.existsSync(binRar)) return binRar;

  const localUnrar = path.join(__dirname, 'unrar.exe');
  if (fs.existsSync(localUnrar)) return localUnrar;

  const binUnrar = path.join(__dirname, 'bin', 'unrar.exe');
  if (fs.existsSync(binUnrar)) return binUnrar;

  return 'rar'; // Fallback para PATH global
}

// Executa comando child_process de forma assíncrona com promise
function runExecutable(cmd, args) {
  return new Promise((resolve) => {
    execFile(cmd, args, { windowsHide: true, maxBuffer: 10 * 1024 * 1024 }, (error, stdout, stderr) => {
      resolve({
        code: error ? error.code || 1 : 0,
        stdout: stdout ? stdout.toString() : '',
        stderr: stderr ? stderr.toString() : '',
      });
    });
  });
}

// Lista os arquivos contidos em um RAR/CBR usando 'rar lb'
async function listRarEntries(rarPath) {
  const rarExe = getRarExecutable();
  const res = await runExecutable(rarExe, ['lb', rarPath]);
  if (res.code === 0 && res.stdout) {
    return res.stdout
      .split(/\r?\n/)
      .map((line) => line.trim())
      .filter((line) => line.length > 0);
  }
  return [];
}

// Extrai somente um arquivo específico de um RAR/CBR
async function extractRarFile(rarPath, targetFileRegex) {
  try {
    const entries = await listRarEntries(rarPath);
    if (entries.length === 0) return null;

    const matchedEntry = entries.find((e) => targetFileRegex.test(e));
    if (!matchedEntry) return null;

    const rarExe = getRarExecutable();
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'readerserver_extract_'));
    const tempDestWithSlash = tempDir.endsWith(path.sep) ? tempDir : tempDir + path.sep;

    // Executa comando de extração de item único: rar e -y "arquivo.cbr" "caminho/ComicInfo.xml" "tempDir\"
    const res = await runExecutable(rarExe, ['e', '-y', rarPath, matchedEntry, tempDestWithSlash]);

    const baseName = path.basename(matchedEntry);
    const extractedFilePath = path.join(tempDir, baseName);

    let extractedContent = null;
    if (fs.existsSync(extractedFilePath)) {
      extractedContent = fs.readFileSync(extractedFilePath, 'utf8');
    }

    // Limpeza de diretório temporário
    try {
      fs.rmSync(tempDir, { recursive: true, force: true });
    } catch (e) {
      // Silenciar erro de limpeza
    }

    if (extractedContent) {
      return {
        fileName: baseName,
        content: extractedContent,
      };
    }
  } catch (err) {
    // Falha silenciosa na extração de rar
  }
  return null;
}

// Extração de arquivos em ZIP/CBZ/EPUB via adm-zip em memória
function extractZipFile(filePath, targetFileRegex) {
  try {
    const zip = new AdmZip(filePath);
    const zipEntries = zip.getEntries();
    const entry = zipEntries.find((e) => targetFileRegex.test(e.entryName));
    if (entry) {
      return {
        fileName: path.basename(entry.entryName),
        content: entry.getData().toString('utf8'),
      };
    }
  } catch (err) {
    // Não é um zip válido ou protegido
  }
  return null;
}

function parseComicInfoXml(xmlContent) {
  try {
    const parsed = xmlParser.parse(xmlContent);
    const root = parsed.ComicInfo || parsed.comicInfo || parsed;
    if (!root) return null;

    const getText = (val) => (val !== undefined && val !== null ? String(val).trim() : null);
    const getFloat = (val) => (val !== undefined && val !== null && !isNaN(parseFloat(val)) ? parseFloat(val) : 0);
    const getInt = (val) => (val !== undefined && val !== null && !isNaN(parseInt(val, 10)) ? parseInt(val, 10) : 0);

    return {
      title: getText(root.Title) || '',
      series: getText(root.Series) || '',
      number: getFloat(root.Number),
      volume: getInt(root.Volume),
      notes: getText(root.Notes),
      year: root.Year ? getInt(root.Year) : null,
      month: root.Month ? getInt(root.Month) : null,
      day: root.Day ? getInt(root.Day) : null,
      writer: getText(root.Writer),
      penciller: getText(root.Penciller),
      inker: getText(root.Inker),
      coverArtist: getText(root.CoverArtist),
      colorist: getText(root.Colorist),
      letterer: getText(root.Letterer),
      publisher: getText(root.Publisher),
      tags: getText(root.Tags),
      web: getText(root.Web),
      editor: getText(root.Editor),
      translator: getText(root.Translator),
      pageCount: root.PageCount ? getInt(root.PageCount) : null,
      count: root.Count ? getInt(root.Count) : null,
      alternateSeries: getText(root.AlternateSeries),
      alternateNumber: root.AlternateNumber ? getFloat(root.AlternateNumber) : null,
      storyArc: getText(root.StoryArc),
      storyArcNumber: getText(root.StoryArcNumber),
      seriesGroup: getText(root.SeriesGroup),
      alternateCount: root.AlternateCount ? getInt(root.AlternateCount) : null,
      summary: getText(root.Summary),
      imprint: getText(root.Imprint),
      genre: getText(root.Genre),
      languageISO: getText(root.LanguageISO) || 'pt',
      format: getText(root.Format),
      characters: getText(root.Characters),
      teams: getText(root.Teams),
      locations: getText(root.Locations),
      scanInformation: getText(root.ScanInformation),
      mainCharacterOrTeam: getText(root.MainCharacterOrTeam),
      review: getText(root.Review),
      manga: root.Manga === 'Yes' || root.Manga === 'YesAndRightToLeft' || root.Manga === true ? 'Yes' : (root.Manga === 'No' ? 'No' : 'Unknown'),
    };
  } catch (err) {
    return null;
  }
}

function parseOpfXml(xmlContent) {
  try {
    const parsed = xmlParser.parse(xmlContent);
    const packageNode = parsed.package || parsed['opf:package'] || parsed;
    const metadataNode = packageNode?.metadata || packageNode?.['opf:metadata'] || {};

    const extractField = (field) => {
      const val = metadataNode[field] || metadataNode[`dc:${field}`] || metadataNode[`opf:${field}`];
      if (!val) return null;
      if (typeof val === 'string') return val.trim();
      if (typeof val === 'number') return String(val);
      if (typeof val === 'object') {
        if (val['#text']) return String(val['#text']).trim();
        if (Array.isArray(val)) {
          return val.map((v) => (typeof v === 'object' ? v['#text'] || '' : v)).join(', ');
        }
      }
      return null;
    };

    return {
      title: extractField('title') || '',
      creator: extractField('creator'),
      contributor: extractField('contributor'),
      publisher: extractField('publisher'),
      datePublished: extractField('date'),
      description: extractField('description'),
      subjects: extractField('subject'),
      language: extractField('language') || 'pt',
      identifiers: extractField('identifier'),
      rights: extractField('rights'),
      relation: extractField('relation'),
    };
  } catch (err) {
    return null;
  }
}

async function findExistingFile(type, fileName) {
  try {
    const endpoint = `${API_BASE_URL}/${type}/search/file-name`;
    const res = await axios.get(endpoint, {
      params: { fileName, size: 1 },
      validateStatus: (status) => status < 500,
    });

    if (res.status === 200 && res.data) {
      const items = res.data._embedded?.[`${type}DtoList`] || res.data._embedded?.[`${type}List`] || res.data.content || [];
      if (items.length > 0) {
        return items[0];
      }
    }
  } catch (err) {
    // Silently ignore or return null if not found
  }
  return null;
}

async function sendRawDataFile(dataPayload) {
  try {
    await axios.post(`${API_BASE_URL}/data`, dataPayload);
  } catch (err) {
    // Log erro se necessário
  }
}

async function processMangaFile(filePath) {
  const fileStat = fs.statSync(filePath);
  const fileName = path.basename(filePath);
  const ext = path.extname(filePath).toLowerCase();
  const rawName = path.basename(filePath, ext);
  const fileDate = formatDateToIso(fileStat.mtime);

  // Extrair ComicInfo dependendo do formato
  let extractedXml = null;
  let comicInfoData = null;

  if (ext === '.cbz' || ext === '.zip') {
    extractedXml = extractZipFile(filePath, /comicinfo\.xml$/i);
  } else if (ext === '.cbr' || ext === '.rar') {
    extractedXml = await extractRarFile(filePath, /comicinfo\.xml$/i);
  }

  if (extractedXml) {
    comicInfoData = parseComicInfoXml(extractedXml.content);
  }

  // Verificar se já existe na API
  const existing = await findExistingFile('manga', fileName);

  let comicInfoId = existing?.comicInfoId || null;

  // 1. Criar ou Atualizar ComicInfo se tiver dados extraídos
  if (comicInfoData) {
    if (!comicInfoData.title) comicInfoData.title = rawName;
    if (!comicInfoData.series) comicInfoData.series = rawName;

    try {
      if (comicInfoId) {
        const updateRes = await axios.put(`${API_BASE_URL}/comicinfo/${comicInfoId}`, comicInfoData);
        comicInfoId = updateRes.data?.id || comicInfoId;
      } else {
        const createRes = await axios.post(`${API_BASE_URL}/comicinfo`, comicInfoData);
        comicInfoId = createRes.data?.id || null;
      }
    } catch (err) {
      // Ignora erro e prossegue
    }
  }

  // 2. Criar ou Atualizar Manga
  const mangaPayload = {
    nome: comicInfoData?.title || rawName,
    fileName: fileName,
    extension: ext.replace('.', ''),
    fileDate: fileDate,
    comicInfoId: comicInfoId,
  };

  let savedManga = null;
  if (existing?.id) {
    const res = await axios.put(`${API_BASE_URL}/manga/${existing.id}`, mangaPayload);
    savedManga = res.data;
  } else {
    const res = await axios.post(`${API_BASE_URL}/manga`, mangaPayload);
    savedManga = res.data;
  }

  // 3. Enviar conteúdo bruto XML para /api/data se houver ComicInfo
  if (extractedXml && comicInfoId) {
    await sendRawDataFile({
      comicInfoId: comicInfoId,
      tipo: 'COMIC_INFO_XML',
      fileName: extractedXml.fileName,
      fileContent: extractedXml.content,
    });
  }

  return { fileName, action: existing ? 'updated' : 'created' };
}

async function processBookFile(filePath) {
  const fileStat = fs.statSync(filePath);
  const fileName = path.basename(filePath);
  const ext = path.extname(filePath).toLowerCase();
  const rawName = path.basename(filePath, ext);
  const fileDate = formatDateToIso(fileStat.mtime);

  // Extrair OPF se for epub
  let extractedOpf = null;
  let opfData = null;

  if (ext === '.epub') {
    extractedOpf = extractZipFile(filePath, /\.opf$/i);
  }

  if (extractedOpf) {
    opfData = parseOpfXml(extractedOpf.content);
  }

  // Verificar se já existe na API
  const existing = await findExistingFile('book', fileName);

  let opfId = existing?.opfId || null;

  // 1. Criar ou Atualizar OPF se tiver dados extraídos
  if (opfData) {
    if (!opfData.title) opfData.title = rawName;

    try {
      if (opfId) {
        const updateRes = await axios.put(`${API_BASE_URL}/opf/${opfId}`, opfData);
        opfId = updateRes.data?.id || opfId;
      } else {
        const createRes = await axios.post(`${API_BASE_URL}/opf`, opfData);
        opfId = createRes.data?.id || null;
      }
    } catch (err) {
      // Ignora erro e prossegue
    }
  }

  // 2. Criar ou Atualizar Book
  const bookPayload = {
    nome: opfData?.title || rawName,
    fileName: fileName,
    extension: ext.replace('.', ''),
    fileDate: fileDate,
    opfId: opfId,
  };

  let savedBook = null;
  if (existing?.id) {
    const res = await axios.put(`${API_BASE_URL}/book/${existing.id}`, bookPayload);
    savedBook = res.data;
  } else {
    const res = await axios.post(`${API_BASE_URL}/book`, bookPayload);
    savedBook = res.data;
  }

  // 3. Enviar conteúdo bruto OPF para /api/data se houver OPF
  if (extractedOpf && opfId) {
    await sendRawDataFile({
      opfId: opfId,
      tipo: 'OPF_XML',
      fileName: extractedOpf.fileName,
      fileContent: extractedOpf.content,
    });
  }

  return { fileName, action: existing ? 'updated' : 'created' };
}

async function main() {
  console.log(chalk.bold.cyan('\n======================================================'));
  console.log(chalk.bold.cyan('           READER SERVER - IMPORTADOR DE MÍDIA         '));
  console.log(chalk.bold.cyan(`           API Base: ${API_BASE_URL}                  `));
  console.log(chalk.bold.cyan('======================================================\n'));

  // Testar conexão com a API via Health Check
  try {
    const healthRes = await axios.get(`${SERVER_URL}/health`, { timeout: 3000 });
    if (healthRes.data.status === 'UP') {
      console.log(chalk.green(`[OK] Conexão com o servidor validada com sucesso.`));
    }
  } catch (err) {
    console.log(chalk.yellow(`[AVISO] Não foi possível validar conexão imediata com ${SERVER_URL}/health. O script tentará prosseguir.\n`));
  }

  // Autenticação
  try {
    console.log(chalk.blue(`\n🔑 Autenticando com usuário '${API_USERNAME}'...`));
    const authRes = await axios.post(`${SERVER_URL}/auth/signin`, {
      username: API_USERNAME,
      password: API_PASSWORD
    }, { timeout: 5000 });

    if (authRes.data && authRes.data.accessToken) {
      axios.defaults.headers.common['Authorization'] = `Bearer ${authRes.data.accessToken}`;
      console.log(chalk.green(`[OK] Autenticado com sucesso.\n`));
    } else {
      console.log(chalk.red(`[ERRO] Falha ao autenticar: Token não retornado.`));
      process.exit(1);
    }
  } catch (err) {
    console.log(chalk.red(`[ERRO] Falha ao autenticar com o servidor. Verifique as credenciais no .env ou se a API está rodando.`));
    console.log(chalk.red(err.message));
    process.exit(1);
  }

  const answers = await inquirer.prompt([
    {
      type: 'list',
      name: 'mediaType',
      message: 'Selecione o tipo de mídia que deseja importar:',
      choices: [
        { name: 'Mangás / Quadrinhos (cbr, cbz, zip, rar, 7z)', value: 'manga' },
        { name: 'Livros (epub, pdf, mobi, azw3)', value: 'book' },
      ],
    },
    {
      type: 'input',
      name: 'targetDir',
      message: 'Informe o caminho da pasta a ser varrida (pastas e subpastas):',
      validate: (input) => {
        if (!input || !fs.existsSync(input.trim())) {
          return 'Caminho inválido ou diretório não encontrado!';
        }
        return true;
      },
    },
  ]);

  const mediaType = answers.mediaType;
  const targetDir = answers.targetDir.trim().replace(/\\/g, '/');
  const allowedExtensions = mediaType === 'manga' ? MANGA_EXTENSIONS : BOOK_EXTENSIONS;

  console.log(chalk.blue(`\n🔍 Varrendo diretório '${targetDir}' recursivamente...`));

  // Buscar todos os arquivos recursivamente
  const allFiles = await glob(`${targetDir}/**/*`, { nodir: true });
  const filteredFiles = allFiles.filter((f) => {
    const ext = path.extname(f).toLowerCase();
    return allowedExtensions.includes(ext);
  });

  const totalFiles = filteredFiles.length;

  if (totalFiles === 0) {
    console.log(chalk.yellow(`\nNenhum arquivo correspondente com extensões [${allowedExtensions.join(', ')}] foi encontrado.`));
    return;
  }

  console.log(chalk.green(`\n✔ Encontrado(s) ${chalk.bold(totalFiles)} arquivo(s) para processamento.\n`));

  const progressBar = new cliProgress.SingleBar(
    {
      format: `${chalk.cyan('{bar}')} {percentage}% | {value}/{total} Arquivos | Atual: {fileName}`,
      barCompleteChar: '\u2588',
      barIncompleteChar: '\u2591',
      hideCursor: true,
    },
    cliProgress.Presets.shades_classic
  );

  progressBar.start(totalFiles, 0, { fileName: 'Iniciando...' });

  let createdCount = 0;
  let updatedCount = 0;
  let errorCount = 0;

  for (let i = 0; i < filteredFiles.length; i++) {
    const file = filteredFiles[i];
    const baseName = path.basename(file);
    const shortName = baseName.length > 30 ? baseName.substring(0, 27) + '...' : baseName;

    progressBar.update(i, { fileName: shortName });

    try {
      let result;
      if (mediaType === 'manga') {
        result = await processMangaFile(file);
      } else {
        result = await processBookFile(file);
      }

      if (result.action === 'created') createdCount++;
      if (result.action === 'updated') updatedCount++;
    } catch (err) {
      errorCount++;
    }

    progressBar.update(i + 1, { fileName: shortName });
  }

  progressBar.stop();

  console.log(chalk.bold.green('\n\n======================================================'));
  console.log(chalk.bold.green('               IMPORTAÇÃO FINALIZADA                  '));
  console.log(chalk.bold.green('======================================================'));
  console.log(chalk.white(`  Total de arquivos:  ${chalk.bold(totalFiles)}`));
  console.log(chalk.cyan(`  Novos importados:   ${chalk.bold(createdCount)}`));
  console.log(chalk.yellow(`  Atualizados:        ${chalk.bold(updatedCount)}`));
  if (errorCount > 0) {
    console.log(chalk.red(`  Erros/Falhas:       ${chalk.bold(errorCount)}`));
  }
  console.log(chalk.bold.green('======================================================\n'));
}

main().catch((err) => {
  console.error(chalk.red('\nOcorreu um erro durante a execução:'), err);
});
