import { describe, it, expect } from 'vitest';
import { parseSearchQuery } from './parseSearchQuery';

describe('parseSearchQuery', () => {
  it('deve retornar raw vazio quando o input for nulo ou em branco', () => {
    expect(parseSearchQuery('')).toEqual({ raw: '' });
    expect(parseSearchQuery('   ')).toEqual({ raw: '' });
  });

  it('deve retornar apenas query quando for texto livre sem tags @', () => {
    const result = parseSearchQuery('clean architecture');
    expect(result).toEqual({
      raw: 'clean architecture',
      query: 'clean architecture'
    });
  });

  it('deve parsear tags únicas e normalizar chaves', () => {
    const result = parseSearchQuery('@autor:Tolkien');
    expect(result.author).toBe('Tolkien');
    expect(result.query).toBeUndefined();
  });

  it('deve parsear valores com aspas contendo espaços', () => {
    const result = parseSearchQuery('@editora:"Editora Novatec" @serie:"Clean Code"');
    expect(result.publisher).toBe('Editora Novatec');
    expect(result.series).toBe('Clean Code');
  });

  it('deve parsear busca mista com texto livre e múltiplas tags', () => {
    const result = parseSearchQuery('computacao @vol:2 @idioma:pt @autor:"Martin Fowler"');
    expect(result.query).toBe('computacao');
    expect(result.volume).toBe('2');
    expect(result.language).toBe('pt');
    expect(result.author).toBe('Martin Fowler');
  });

  it('deve normalizar corretamente os aliases em português e inglês', () => {
    const result1 = parseSearchQuery('@roteirista:Oda @ilustrador:Murata @gênero:Shonen');
    expect(result1.writer).toBe('Oda');
    expect(result1.illustrator).toBe('Murata');
    expect(result1.genre).toBe('Shonen');

    const result2 = parseSearchQuery('@edição:10 @arquivo:capitulo_10.cbz @tipo:epub');
    expect(result2.volume).toBe('10');
    expect(result2.arquivo).toBe('capitulo_10.cbz');
    expect(result2.type).toBe('epub');
  });
});
