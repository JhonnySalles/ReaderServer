import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { FilterBar, SearchSuggestion } from './FilterBar';

describe('FilterBar Component', () => {
  const mockSuggestions: SearchSuggestion[] = [
    { command: 'autor', label: 'Autor / Escritor', example: '@autor:Nome', description: 'Filtra por autor' },
    { command: 'serie', label: 'Série / Obra', example: '@serie:Nome', description: 'Filtra por série' }
  ];

  it('deve renderizar o input de busca e o botão de ordenação', () => {
    render(
      <FilterBar
        searchQuery=""
        onSearchChange={() => {}}
        direction="asc"
        onDirectionToggle={() => {}}
        placeholder="Pesquisar..."
      />
    );

    expect(screen.getByPlaceholderText('Pesquisar...')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /ordem: crescente/i })).toBeInTheDocument();
  });

  it('deve chamar onSearchChange ao digitar no campo de busca', async () => {
    const handleSearchChange = vi.fn();
    const user = userEvent.setup();

    render(
      <FilterBar
        searchQuery=""
        onSearchChange={handleSearchChange}
        direction="asc"
        onDirectionToggle={() => {}}
      />
    );

    const input = screen.getByRole('textbox');
    await user.type(input, 'a');

    expect(handleSearchChange).toHaveBeenCalledWith('a');
  });

  it('deve alternar a direção ao clicar no botão de ordenação', async () => {
    const handleToggle = vi.fn();
    const user = userEvent.setup();

    render(
      <FilterBar
        searchQuery=""
        onSearchChange={() => {}}
        direction="desc"
        onDirectionToggle={handleToggle}
      />
    );

    const sortButton = screen.getByRole('button', { name: /ordem: decrescente/i });
    await user.click(sortButton);

    expect(handleToggle).toHaveBeenCalledTimes(1);
  });

  it('deve limpar a busca ao clicar no botão de limpar (X)', async () => {
    const handleSearchChange = vi.fn();
    const user = userEvent.setup();

    render(
      <FilterBar
        searchQuery="termo de busca"
        onSearchChange={handleSearchChange}
        direction="asc"
        onDirectionToggle={() => {}}
      />
    );

    const clearButton = screen.getByRole('button', { name: /limpar busca/i });
    await user.click(clearButton);

    expect(handleSearchChange).toHaveBeenCalledWith('');
  });
});
