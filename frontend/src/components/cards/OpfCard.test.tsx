import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { OpfCard } from './OpfCard';
import type { OpfItem } from '../../types/api';

describe('OpfCard Component', () => {
  const mockOpf: OpfItem = {
    id: 'opf-456',
    title: 'O Guia do Mochileiro das Galáxias',
    creator: 'Douglas Adams',
    publisher: 'Editora Arqueiro',
    datePublished: '1979-10-12',
    language: 'pt',
    subjects: 'Ficção Científica, Humor'
  };

  it('deve renderizar os detalhes de metadados do OPF', () => {
    render(<OpfCard opf={mockOpf} />);

    expect(screen.getByRole('heading', { level: 3, name: 'O Guia do Mochileiro das Galáxias' })).toBeInTheDocument();
    expect(screen.getByText('Douglas Adams')).toBeInTheDocument();
    expect(screen.getByText('Editora Arqueiro')).toBeInTheDocument();
    expect(screen.getByText('12/10/1979')).toBeInTheDocument();
    expect(screen.getByText('PT')).toBeInTheDocument();
    expect(screen.getByText('Ficção Científica, Humor')).toBeInTheDocument();
  });

  it('deve chamar onClick ao clicar no card', async () => {
    const handleClick = vi.fn();
    const user = userEvent.setup();

    render(<OpfCard opf={mockOpf} onClick={handleClick} />);

    const titleElement = screen.getByRole('heading', { level: 3, name: 'O Guia do Mochileiro das Galáxias' });
    await user.click(titleElement);

    expect(handleClick).toHaveBeenCalledTimes(1);
  });

  it('deve renderizar o botão de download do arquivo .opf', () => {
    render(<OpfCard opf={mockOpf} />);

    const downloadBtn = screen.getByRole('button', { name: /baixar arquivo \.opf/i });
    expect(downloadBtn).toBeInTheDocument();
  });
});
