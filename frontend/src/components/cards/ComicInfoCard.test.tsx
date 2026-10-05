import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { ComicInfoCard } from './ComicInfoCard';
import type { ComicInfoItem } from '../../types/api';

describe('ComicInfoCard Component', () => {
  const mockComicInfo: ComicInfoItem = {
    id: 'comicinfo-123',
    title: 'Batman: The Long Halloween',
    series: 'Batman',
    number: 1,
    volume: 1,
    writer: 'Jeph Loeb',
    year: 1996,
    languageISO: 'en',
    genre: 'Superhero / Mystery'
  };

  it('deve renderizar os detalhes completos do ComicInfo', () => {
    render(<ComicInfoCard comicInfo={mockComicInfo} />);

    expect(screen.getByRole('heading', { level: 3, name: 'Batman: The Long Halloween' })).toBeInTheDocument();
    expect(screen.getByText(/Série: Batman #1/)).toBeInTheDocument();
    expect(screen.getByText('Vol. 1')).toBeInTheDocument();
    expect(screen.getByText('Jeph Loeb')).toBeInTheDocument();
    expect(screen.getByText('1996')).toBeInTheDocument();
    expect(screen.getByText('EN')).toBeInTheDocument();
    expect(screen.getByText('Superhero / Mystery')).toBeInTheDocument();
  });

  it('deve chamar onClick ao clicar no card', async () => {
    const handleClick = vi.fn();
    const user = userEvent.setup();

    render(<ComicInfoCard comicInfo={mockComicInfo} onClick={handleClick} />);

    const titleElement = screen.getByRole('heading', { level: 3, name: 'Batman: The Long Halloween' });
    await user.click(titleElement);

    expect(handleClick).toHaveBeenCalledTimes(1);
  });

  it('deve renderizar o botão de download do ComicInfo.xml', () => {
    render(<ComicInfoCard comicInfo={mockComicInfo} />);

    const downloadBtn = screen.getByRole('button', { name: /baixar comicinfo\.xml/i });
    expect(downloadBtn).toBeInTheDocument();
  });
});
