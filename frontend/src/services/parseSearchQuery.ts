/**
 * Utilitário para parsear queries de pesquisa com tags/comandos do tipo `@comando:valor` ou `@comando:"valor com espaços"`.
 * Compatível com o formato do BilingualReader Android (Kotlin).
 */

export interface ParsedSearchQuery {
  raw: string;
  query?: string;
  [key: string]: string | undefined;
}

/**
 * Normaliza os aliases comuns de filtros para os campos aceitos na API.
 */
function normalizeKey(key: string): string {
  const lower = key.toLowerCase();
  switch (lower) {
    case 'author':
    case 'autor':
    case 'creator':
    case 'criador':
      return 'author';
    case 'writer':
    case 'roteirista':
      return 'writer';
    case 'illustrator':
    case 'ilustrador':
    case 'desenhista':
    case 'penciller':
      return 'illustrator';
    case 'serie':
    case 'series':
    case 'série':
      return 'series';
    case 'volume':
    case 'vol':
    case 'edicao':
    case 'edição':
    case 'number':
    case 'numero':
    case 'número':
      return 'volume';
    case 'publisher':
    case 'editora':
      return 'publisher';
    case 'genre':
    case 'genero':
    case 'gênero':
    case 'generos':
    case 'gêneros':
      return 'genre';
    case 'title':
    case 'titulo':
    case 'título':
      return 'title';
    case 'language':
    case 'idioma':
      return 'language';
    case 'subjects':
    case 'subject':
    case 'assunto':
    case 'assuntos':
    case 'tags':
    case 'tag':
      return 'tags';
    case 'type':
    case 'tipo':
    case 'ext':
    case 'extensao':
    case 'extensão':
    case 'extension':
      return 'type';
    case 'sinopse':
    case 'synopsis':
    case 'resumo':
    case 'summary':
    case 'description':
    case 'descricao':
    case 'descrição':
      return 'summary';
    case 'publicacao':
    case 'publicação':
    case 'data':
    case 'date':
      return 'date';
    case 'classificacao':
    case 'classificação':
    case 'rating':
    case 'agerating':
      return 'ageRating';
    case 'contributor':
    case 'contribuitor':
    case 'contribuidor':
      return 'contributor';
    case 'identificadores':
    case 'identificador':
    case 'identifiers':
    case 'identifier':
    case 'isbn':
      return 'identifiers';
    default:
      return lower;
  }
}

export function parseSearchQuery(input: string): ParsedSearchQuery {
  if (!input || !input.trim()) {
    return { raw: '' };
  }

  const result: ParsedSearchQuery = { raw: input };
  let freeText = input;

  // Regex compatível: (@\S*:("[^"]*"|[^\s]+))\s*
  const tagRegex = /(@\S*:("[^"]*"|[^\s]+))\s*/g;
  let match: RegExpExecArray | null;

  while ((match = tagRegex.exec(input)) !== null) {
    const fullMatch = match[0];
    const content = match[1];

    // Remove a tag do texto livre
    freeText = freeText.replace(fullMatch, ' ');

    const colonIndex = content.indexOf(':');
    if (colonIndex > 1) {
      const tagKey = content.substring(1, colonIndex); // remove o '@'
      let tagValue = content.substring(colonIndex + 1);
      // Remove aspas se houver
      if (tagValue.startsWith('"') && tagValue.endsWith('"')) {
        tagValue = tagValue.slice(1, -1);
      }

      if (tagKey && tagValue) {
        const normalizedKey = normalizeKey(tagKey);
        result[normalizedKey] = tagValue;
      }
    }
  }

  const cleanQuery = freeText.trim();
  if (cleanQuery.length > 0) {
    result.query = cleanQuery;
  }

  return result;
}
