import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import { Sidebar } from './Sidebar';

describe('Sidebar Component', () => {
  it('deve renderizar os links de navegação e o logo no modo aberto', () => {
    render(
      <MemoryRouter>
        <Sidebar isOpen={true} onToggle={() => {}} />
      </MemoryRouter>
    );

    expect(screen.getByText('Reader Server')).toBeInTheDocument();
    expect(screen.getByText('Início')).toBeInTheDocument();
    expect(screen.getByText('Livros & Epubs')).toBeInTheDocument();
    expect(screen.getByText('Mangás & Comics')).toBeInTheDocument();
    expect(screen.getByText('Sincronizar Acesso')).toBeInTheDocument();
  });

  it('deve chamar onToggle ao clicar no botão de recolher', async () => {
    const handleToggle = vi.fn();
    const user = userEvent.setup();

    render(
      <MemoryRouter>
        <Sidebar isOpen={true} onToggle={handleToggle} />
      </MemoryRouter>
    );

    const toggleBtn = screen.getByTitle('Recolher menu');
    await user.click(toggleBtn);

    expect(handleToggle).toHaveBeenCalledTimes(1);
  });

  it('deve exibir botão de expandir no modo recolhido', async () => {
    const handleToggle = vi.fn();
    const user = userEvent.setup();

    render(
      <MemoryRouter>
        <Sidebar isOpen={false} onToggle={handleToggle} />
      </MemoryRouter>
    );

    const expandBtn = screen.getByTitle('Expandir menu');
    expect(expandBtn).toBeInTheDocument();

    await user.click(expandBtn);
    expect(handleToggle).toHaveBeenCalledTimes(1);
  });
});
