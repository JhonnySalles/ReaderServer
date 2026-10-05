import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MediaGrid } from './MediaGrid';

describe('MediaGrid Component', () => {
  it('deve renderizar os elementos filhos corretamente', () => {
    render(
      <MediaGrid loading={false} hasMore={false} lastElementRef={() => {}} count={2}>
        <div data-testid="card-1">Card 1</div>
        <div data-testid="card-2">Card 2</div>
      </MediaGrid>
    );

    expect(screen.getByTestId('card-1')).toBeInTheDocument();
    expect(screen.getByTestId('card-2')).toBeInTheDocument();
  });

  it('deve exibir mensagem de estado vazio quando count for zero e loading for false', () => {
    render(
      <MediaGrid
        loading={false}
        hasMore={false}
        lastElementRef={() => {}}
        count={0}
        emptyMessage="Nenhum livro cadastrado."
      >
        <div />
      </MediaGrid>
    );

    expect(screen.getByText('Sem resultados')).toBeInTheDocument();
    expect(screen.getByText('Nenhum livro cadastrado.')).toBeInTheDocument();
  });

  it('deve exibir indicador de carregamento quando loading for true', () => {
    render(
      <MediaGrid loading={true} hasMore={true} lastElementRef={() => {}} count={5}>
        <div />
      </MediaGrid>
    );

    expect(screen.getByText('Carregando mais itens...')).toBeInTheDocument();
  });

  it('deve exibir mensagem de fim de resultados quando não houver mais páginas', () => {
    render(
      <MediaGrid loading={false} hasMore={false} lastElementRef={() => {}} count={15}>
        <div />
      </MediaGrid>
    );

    expect(screen.getByText(/Você visualizou todos os 15 registros/i)).toBeInTheDocument();
  });

  it('deve vincular a função lastElementRef na sentinela de rolagem', () => {
    const mockRef = vi.fn();

    render(
      <MediaGrid loading={false} hasMore={true} lastElementRef={mockRef} count={10}>
        <div />
      </MediaGrid>
    );

    expect(mockRef).toHaveBeenCalled();
  });
});
