import api from './api';

/**
 * Utilitário para realizar o download autenticado de arquivos via API do ReaderServer
 * e disparar o salvamento nativo no navegador do usuário.
 */
export async function downloadFile(url: string, defaultFilename: string = 'arquivo.xml'): Promise<boolean> {
  try {
    const response = await api.get(url, {
      responseType: 'blob',
      headers: {
        'Accept': 'application/xml, text/xml, text/plain, application/octet-stream, */*'
      }
    });

    // Tentar extrair o nome do arquivo a partir do header Content-Disposition
    let filename = defaultFilename;
    const disposition = response.headers['content-disposition'] || response.headers['Content-Disposition'];
    if (disposition) {
      // Suporte para filename*=UTF-8''... ou filename="..."
      const utf8FilenameRegex = /filename\*=UTF-8''([^;]+)/i;
      const utf8Matches = utf8FilenameRegex.exec(disposition);
      if (utf8Matches && utf8Matches[1]) {
        filename = decodeURIComponent(utf8Matches[1]);
      } else {
        const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/i;
        const matches = filenameRegex.exec(disposition);
        if (matches && matches[1]) {
          filename = matches[1].replace(/['"]/g, '').trim();
        }
      }
    }

    const contentTypeHeader = response.headers['content-type'] || response.headers['Content-Type'];
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
  } catch (error: any) {
    let errorMessage = 'Erro ao baixar arquivo. Verifique se o servidor está ativo ou se o arquivo existe.';
    if (error?.response?.data instanceof Blob) {
      try {
        const text = await error.response.data.text();
        const json = JSON.parse(text);
        if (json.message) {
          errorMessage = `Erro no download: ${json.message}`;
        }
      } catch {
        // Fallback para mensagem padrão
      }
    }
    console.error('Erro ao efetuar download:', error);
    alert(errorMessage);
    return false;
  }
}
