import api from './api';

/**
 * Utilitário para realizar o download autenticado de arquivos via API do ReaderServer
 * e disparar o salvamento nativo no navegador do usuário.
 */
export async function downloadFile(url: string, defaultFilename: string = 'arquivo.xml'): Promise<boolean> {
  try {
    const response = await api.get(url, {
      responseType: 'blob'
    });

    // Tentar extrair o nome do arquivo a partir do header Content-Disposition
    let filename = defaultFilename;
    const disposition = response.headers['content-disposition'];
    if (disposition && disposition.indexOf('filename=') !== -1) {
      const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
      const matches = filenameRegex.exec(disposition);
      if (matches != null && matches[1]) {
        filename = matches[1].replace(/['"]/g, '');
      }
    }

    const contentTypeHeader = response.headers['content-type'];
    const contentType = typeof contentTypeHeader === 'string' ? contentTypeHeader : 'application/xml';
    const blob = new Blob([response.data], { type: contentType });
    const downloadUrl = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = downloadUrl;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(downloadUrl);
    return true;
  } catch (error) {
    console.error('Erro ao efetuar download:', error);
    alert('Erro ao baixar arquivo. Verifique se o servidor está ativo ou se o arquivo existe.');
    return false;
  }
}
