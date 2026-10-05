import { describe, it, expect } from 'vitest';
import { formatDate } from './formatters';

describe('Formatters - formatDate', () => {
  it('deve retornar "N/A" para strings nulas, indefinidas ou vazias', () => {
    expect(formatDate(null)).toBe('N/A');
    expect(formatDate(undefined)).toBe('N/A');
    expect(formatDate('')).toBe('N/A');
    expect(formatDate('   ')).toBe('N/A');
  });

  it('deve retornar o próprio ano quando a string for apenas 4 dígitos', () => {
    expect(formatDate('2023')).toBe('2023');
    expect(formatDate('1999')).toBe('1999');
  });

  it('deve retornar a data no formato DD/MM/AAAA para entradas no formato YYYY-MM-DD', () => {
    expect(formatDate('2023-12-25')).toBe('25/12/2023');
    expect(formatDate('2020-01-05')).toBe('05/01/2020');
  });

  it('deve manter apenas a parte da data se a string já contiver DD/MM/AAAA com horário', () => {
    expect(formatDate('15/08/2022 14:30:00')).toBe('15/08/2022');
    expect(formatDate('01/01/2021')).toBe('01/01/2021');
  });

  it('deve formatar datas completas no padrão ISO', () => {
    const result = formatDate('2023-05-10T15:30:00Z');
    expect(result).toMatch(/\d{2}\/\d{2}\/2023/);
  });
});
