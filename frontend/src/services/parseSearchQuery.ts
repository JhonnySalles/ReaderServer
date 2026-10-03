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
    case 'writer':
    case 'roteirista':
      return 'author';
    case 'serie':
    case 'series':
      return 'series';
    case 'volume':
    case 'vol':
      return 'volume';
    case 'publisher':
    case 'editora':
      return 'publisher';
    case 'genre':
    case 'genero':
    case 'generos':
      return 'genre';
    case 'creator':
    case 'criador':
      return 'creator';
    case 'title':
    case 'titulo':
      return 'title';
    case 'language':
    case 'idioma':
      return 'language';
    case 'subjects':
    case 'subject':
    case 'assunto':
    case 'tags':
      return 'subjects';
    case 'type':
    case 'tipo':
    case 'ext':
    case 'extension':
      return 'type';
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
