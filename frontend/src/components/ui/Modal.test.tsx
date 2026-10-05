import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Modal } from './Modal';

describe('Modal Component', () => {
  it('não deve renderizar nada quando isOpen for false', () => {
    render(
      <Modal isOpen={false} onClose={() => {}} title="Título Teste">
        <p>Conteúdo do Modal</p>
      </Modal>
    );

    expect(screen.queryByText('Título Teste')).not.toBeInTheDocument();
    expect(screen.queryByText('Conteúdo do Modal')).not.toBeInTheDocument();
  });

  it('deve renderizar título e conteúdo quando isOpen for true', () => {
    render(
      <Modal isOpen={true} onClose={() => {}} title="Detalhes do Livro">
        <p>Conteúdo Exibido</p>
      </Modal>
    );

    expect(screen.getByText('Detalhes do Livro')).toBeInTheDocument();
    expect(screen.getByText('Conteúdo Exibido')).toBeInTheDocument();
  });

  it('deve disparar onClose ao clicar no botão de fechar (X)', async () => {
    const handleClose = vi.fn();
    const user = userEvent.setup();

    render(
      <Modal isOpen={true} onClose={handleClose} title="Modal Fechar">
        <p>Conteúdo</p>
      </Modal>
    );

    const closeBtn = screen.getByRole('button', { name: /fechar modal/i });
    await user.click(closeBtn);

    expect(handleClose).toHaveBeenCalledTimes(1);
  });

  it('deve disparar onDownload ao clicar no botão de baixar', async () => {
    const handleDownload = vi.fn();
    const user = userEvent.setup();

    render(
      <Modal
        isOpen={true}
        onClose={() => {}}
        title="Download Teste"
        onDownload={handleDownload}
        downloadLabel="Baixar OPF"
      >
        <p>Conteúdo</p>
      </Modal>
    );

    const downloadBtn = screen.getByRole('button', { name: /baixar opf/i });
    await user.click(downloadBtn);

    expect(handleDownload).toHaveBeenCalledTimes(1);
  });

  it('deve disparar onDelete ao clicar no botão de excluir', async () => {
    const handleDelete = vi.fn();
    const user = userEvent.setup();

    render(
      <Modal
        isOpen={true}
        onClose={() => {}}
        title="Excluir Teste"
        onDelete={handleDelete}
        deleteLabel="Excluir Registro"
      >
        <p>Conteúdo</p>
      </Modal>
    );

    const deleteBtn = screen.getByRole('button', { name: /excluir registro/i });
    await user.click(deleteBtn);

    expect(handleDelete).toHaveBeenCalledTimes(1);
  });
});
