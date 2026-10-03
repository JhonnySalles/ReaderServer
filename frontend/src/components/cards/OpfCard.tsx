import React, { useState } from 'react';
import { Tag, Book, User, Globe, Calendar, Download, Loader2 } from 'lucide-react';
import type { OpfItem } from '../../types/api';
import { downloadFile } from '../../services/downloadService';
import './Card.css';

interface OpfCardProps {
  opf: OpfItem;
  onClick?: () => void;
}

export const OpfCard: React.FC<OpfCardProps> = ({ opf, onClick }) => {
  const [downloading, setDownloading] = useState(false);

  const handleDownload = async (e: React.MouseEvent) => {
    e.stopPropagation();
    if (!opf.id || downloading) return;
    setDownloading(true);
    const cleanName = (opf.title || 'content').replace(/[/\\?%*:|"<>]/g, '_');
    await downloadFile(`/api/opf/${opf.id}/download`, `${cleanName}.opf`);
    setDownloading(false);
  };

  return (
    <div className="media-card glass-panel opf-card-border" onClick={onClick}>
      <div className="card-top-actions">
        <button
          className="card-download-btn"
          onClick={handleDownload}
          title="Baixar arquivo .opf"
          disabled={downloading}
        >
          {downloading ? <Loader2 size={14} className="spin-loader" /> : <Download size={14} />}
        </button>
        <div className="card-badge opf-badge">
          <Book size={14} />
          <span>OPF METADATA</span>
        </div>
      </div>

      <div className="card-body">
        <h3 className="card-title" title={opf.title || 'Sem título'}>
          {opf.title || 'Sem título'}
        </h3>

        <div className="card-filename">
          <User size={14} className="card-subicon" />
          <span>{opf.creator || 'Autor desconhecido'}</span>
        </div>

        <div className="card-meta">
          {opf.publisher && (
            <div className="meta-item">
              <span>{opf.publisher}</span>
            </div>
          )}
          {opf.datePublished && (
            <div className="meta-item">
              <Calendar size={14} />
              <span>{opf.datePublished}</span>
            </div>
          )}
          {opf.language && (
            <div className="meta-item">
              <Globe size={14} />
              <span>{opf.language.toUpperCase()}</span>
            </div>
          )}
        </div>

        {opf.subjects && (
          <div className="card-tags">
            <Tag size={12} />
            <span>{opf.subjects}</span>
          </div>
        )}
      </div>
    </div>
  );
};
