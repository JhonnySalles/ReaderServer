export interface PageableResponse<T> {
  _embedded?: {
    mangaDtoList?: T[];
    bookDtoList?: T[];
    comicInfoDtoList?: T[];
    opfDtoList?: T[];
    dataDtoList?: T[];
  };
  page?: {
    size: number;
    totalElements: number;
    totalPages: number;
    number: number;
  };
}

export interface MangaItem {
  id?: string;
  nome?: string;
  fileName?: string;
  extension?: string;
  fileDate?: string;
  comicInfoId?: string;
  comicInfo?: ComicInfoItem;
}

export interface ComicInfoItem {
  id?: string;
  idMal?: number;
  comic?: string;
  title?: string;
  series?: string;
  number?: number;
  volume?: number;
  notes?: string;
  year?: number;
  month?: number;
  day?: number;
  writer?: string;
  penciller?: string;
  publisher?: string;
  genre?: string;
  languageISO?: string;
  ageRating?: string;
  manga?: string;
  summary?: string;
}

export interface BookItem {
  id?: string;
  nome?: string;
  fileName?: string;
  extension?: string;
  fileDate?: string;
  opfId?: string;
  opf?: OpfItem;
}

export interface OpfItem {
  id?: string;
  title?: string;
  creator?: string;
  contributor?: string;
  publisher?: string;
  datePublished?: string;
  description?: string;
  subjects?: string;
  language?: string;
  identifiers?: string;
  series?: string;
  seriesIndex?: string;
  rights?: string;
  relation?: string;
}
