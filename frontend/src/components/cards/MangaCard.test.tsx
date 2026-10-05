import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MangaCard } from './MangaCard';
import type { MangaItem } from '../../types/api';

describe('MangaCard Component', () => {
  const mockManga: MangaItem = {
    id: 'manga-123',
    nome: 'One Piece',
    fileName: 'one_piece_vol01.cbz',
    extension: 'cbz',
    volume: 1,
    fileDate: '2023-03-20T14:00:00Z',
    comicInfoId: 'comic-123',
    comicInfo: {
      id: 'comic-123',
      title: 'Romance Dawn',
      series: 'One Piece',
      publisher: 'Shueisha',
      languageISO: 'ja'
    }
  };

  it('deve renderizar o título, nome do arquivo e metadados do mangá', () => {
    render(<MangaCard manga={mockManga} />);

    expect(screen.getByRole('heading', { level: 3, name: 'One Piece' })).toBeInTheDocument();
    expect(screen.getByText('one_piece_vol01.cbz')).toBeInTheDocument();
    expect(screen.getByText('CBZ')).toBeInTheDocument();
    expect(screen.getByText('Vol. 1')).toBeInTheDocument();
    expect(screen.getByText('ComicInfo OK')).toBeInTheDocument();
    expect(screen.getByText(/Shueisha/)).toBeInTheDocument();
  });

  it('deve chamar onClick ao clicar no card de manga', async () => {
    const handleClick = vi.fn();
    const user = userEvent.setup();

    render(<MangaCard manga={mockManga} onClick={handleClick} />);

    const titleElement = screen.getByRole('heading', { level: 3, name: 'One Piece' });
    await user.click(titleElement);

    expect(handleClick).toHaveBeenCalledTimes(1);
  });

  it('deve renderizar o botão de download quando houver comicInfoId', () => {
    render(<MangaCard manga={mockManga} />);

    const downloadBtn = screen.getByRole('button', { name: /baixar comicinfo vinculado/i });
    expect(downloadBtn).toBeInTheDocument();
  });
});
