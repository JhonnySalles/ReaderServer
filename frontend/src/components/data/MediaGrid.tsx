import React from 'react';
import { Loader2, AlertCircle } from 'lucide-react';
import './MediaGrid.css';

interface MediaGridProps {
  children: React.ReactNode;
  loading: boolean;
  hasMore: boolean;
  lastElementRef: (node: HTMLDivElement | null) => void;
  emptyMessage?: string;
  count: number;
}

export const MediaGrid: React.FC<MediaGridProps> = ({
  children,
  loading,
  hasMore,
  lastElementRef,
  emptyMessage = "Nenhum registro encontrado.",
  count
}) => {
  return (
    <div className="media-grid-wrapper">
      {count === 0 && !loading && (
        <div className="empty-state glass-panel">
          <AlertCircle size={36} className="empty-icon" />
          <p className="empty-title">Sem resultados</p>
          <p className="empty-desc">{emptyMessage}</p>
        </div>
      )}

      <div className="media-grid">
        {children}
      </div>

      {/* Sentinel para o Infinite Scroll */}
      <div ref={lastElementRef} className="scroll-sentinel" />

      {loading && (
        <div className="loading-state">
          <Loader2 size={32} className="spin-loader" />
          <span>Carregando mais itens...</span>
        </div>
      )}

      {!hasMore && count > 0 && (
        <div className="end-of-results">
          <span>Você visualizou todos os {count} registros</span>
        </div>
      )}
    </div>
  );
};
