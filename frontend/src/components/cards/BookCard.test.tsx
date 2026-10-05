import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BookCard } from './BookCard';
import type { BookItem } from '../../types/api';

describe('BookCard Component', () => {
  const mockBook: BookItem = {
    id: 'book-123',
    nome: 'Clean Architecture',
    fileName: 'clean_arch.epub',
    extension: 'epub',
    volume: 1,
    fileDate: '2023-01-15T10:00:00Z',
    opfId: 'opf-123',
    opf: {
      id: 'opf-123',
      title: 'Clean Architecture Guide',
      creator: 'Robert C. Martin',
      publisher: 'Prentice Hall',
      language: 'en'
    }
  };

  it('deve renderizar o título, nome do arquivo e metadados do livro', () => {
    render(<BookCard book={mockBook} />);

    expect(screen.getByText('Clean Architecture')).toBeInTheDocument();
    expect(screen.getByText('clean_arch.epub')).toBeInTheDocument();
    expect(screen.getByText('EPUB')).toBeInTheDocument();
    expect(screen.getByText('Vol. 1')).toBeInTheDocument();
    expect(screen.getByText('OPF OK')).toBeInTheDocument();
    expect(screen.getByText(/Robert C\. Martin/)).toBeInTheDocument();
    expect(screen.getByText(/Prentice Hall/)).toBeInTheDocument();
  });

  it('deve chamar onClick ao clicar no card', async () => {
    const handleClick = vi.fn();
    const user = userEvent.setup();

    render(<BookCard book={mockBook} onClick={handleClick} />);

    const card = screen.getByText('Clean Architecture');
    await user.click(card);

    expect(handleClick).toHaveBeenCalledTimes(1);
  });

  it('deve renderizar o botão de download quando houver opfId', () => {
    render(<BookCard book={mockBook} />);

    const downloadBtn = screen.getByRole('button', { name: /baixar opf vinculado/i });
    expect(downloadBtn).toBeInTheDocument();
  });
});
