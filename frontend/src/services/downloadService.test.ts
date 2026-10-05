import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { downloadFile } from './downloadService';
import api from './api';

describe('downloadService - downloadFile', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('deve realizar download com sucesso e disparar a criação do link no DOM', async () => {
    const mockBlob = new Blob(['<xml>conteudo</xml>'], { type: 'application/xml' });
    const mockGet = vi.spyOn(api, 'get').mockResolvedValueOnce({
      data: mockBlob,
      headers: {
        'content-disposition': 'attachment; filename="meu_livro.opf"',
        'content-type': 'application/xml'
      }
    });

    const createObjectURLMock = vi.fn().mockReturnValue('blob:http://localhost/mock-url');
    const revokeObjectURLMock = vi.fn();
    window.URL.createObjectURL = createObjectURLMock;
    window.URL.revokeObjectURL = revokeObjectURLMock;

    const result = await downloadFile('/api/book/123/download', 'padrao.opf');

    expect(result).toBe(true);
    expect(mockGet).toHaveBeenCalledWith('/api/book/123/download', expect.objectContaining({
      responseType: 'blob'
    }));
    expect(createObjectURLMock).toHaveBeenCalled();
    expect(revokeObjectURLMock).toHaveBeenCalled();
  });

  it('deve tratar falha na requisição e retornar false', async () => {
    vi.spyOn(api, 'get').mockRejectedValueOnce(new Error('Erro de rede'));
    vi.spyOn(window, 'alert').mockImplementation(() => {});

    const result = await downloadFile('/api/book/erro/download');

    expect(result).toBe(false);
  });
});
