import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import { Sidebar } from './Sidebar';
import { authStorage } from '../../services/api';

describe('Sidebar Component', () => {
  beforeEach(() => {
    sessionStorage.clear();
  });

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
    expect(screen.getByText('Fazer Login')).toBeInTheDocument();
  });

  it('deve exibir botão de Sair quando usuário estiver autenticado', () => {
    authStorage.saveAuth({
      username: 'admin',
      authenticated: true,
      accessToken: 'fake-token',
      refreshToken: 'fake-refresh'
    });

    render(
      <MemoryRouter>
        <Sidebar isOpen={true} onToggle={() => {}} />
      </MemoryRouter>
    );

    expect(screen.getByText('Sair / Trocar Usuário')).toBeInTheDocument();
    expect(screen.getByText('Sessão Ativa')).toBeInTheDocument();
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
