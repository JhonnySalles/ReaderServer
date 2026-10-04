/**
 * Formata uma string de data para o padrão 'DD/MM/AAAA'.
 * Lida com formatos ISO (ex: 2019-12-17T03:00:00+00:00, 2019-12-17),
 * timestamp numérico ou strings parciais.
 */
export function formatDate(dateStr?: string | null): string {
  if (!dateStr || !dateStr.trim()) return 'N/A';

  const trimmed = dateStr.trim();

  // Se for apenas ano (ex: "2019")
  if (/^\d{4}$/.test(trimmed)) {
    return trimmed;
  }

  // Se já estiver no padrão DD/MM/AAAA ou DD/MM/AAAA HH:mm
  if (/^\d{2}\/\d{2}\/\d{4}/.test(trimmed)) {
    return trimmed.split(' ')[0];
  }

  // Tenta parsear como data ISO ou padrão
  const parsed = new Date(trimmed);
  if (!isNaN(parsed.getTime())) {
    // Se a string contiver apenas YYYY-MM-DD, o parse UTC pode mudar o dia dependendo do timezone local
    // Para evitar mudar o dia em 'YYYY-MM-DD', se for YYYY-MM-DD fazemos direto
    const ymdMatch = trimmed.match(/^(\d{4})-(\d{2})-(\d{2})/);
    if (ymdMatch) {
      const [, year, month, day] = ymdMatch;
      return `${day}/${month}/${year}`;
    }

    return parsed.toLocaleDateString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
  }

  return trimmed;
}
