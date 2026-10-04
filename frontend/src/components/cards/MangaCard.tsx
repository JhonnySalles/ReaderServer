import React, { useState } from 'react';
import { Layers, Calendar, HardDrive, Info, Download, Loader2 } from 'lucide-react';
import type { MangaItem } from '../../types/api';
import { downloadFile } from '../../services/downloadService';
import './Card.css';

interface MangaCardProps {
  manga: MangaItem;
  onClick?: () => void;
}

export const MangaCard: React.FC<MangaCardProps> = ({ manga, onClick }) => {
  const [downloading, setDownloading] = useState(false);
  const formattedDate = manga.fileDate 
    ? new Date(manga.fileDate).toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit', year: 'numeric' })
    : 'Sem data';

  const handleDownload = async (e: React.MouseEvent) => {
    e.stopPropagation();
    if (!manga.id || downloading) return;
    setDownloading(true);
    const cleanName = (manga.nome || manga.fileName || 'ComicInfo').replace(/[/\\?%*:|"<>]/g, '_');
    await downloadFile(`/api/manga/${manga.id}/download`, `${cleanName}.xml`);
    setDownloading(false);
  };

  return (
    <div className="media-card glass-panel manga-card-border" onClick={onClick}>
      <div className="card-top-actions">
        {manga.comicInfoId && (
          <button
            className="card-download-btn"
            onClick={handleDownload}
            title="Baixar ComicInfo vinculado (XML)"
            disabled={downloading}
          >
            {downloading ? <Loader2 size={14} className="spin-loader" /> : <Download size={14} />}
          </button>
        )}
        <div className="card-badge manga-badge">
          <Layers size={14} />
          <span>{manga.extension ? manga.extension.toUpperCase() : 'MANGA'}</span>
        </div>
      </div>

      <div className="card-body">
        <h3 className="card-title" title={manga.nome || manga.fileName || 'Sem título'}>
          {manga.nome || manga.fileName || 'Sem título'}
        </h3>

        <div className="card-filename" title={manga.fileName || ''}>
          <HardDrive size={14} className="card-subicon" />
          <span>{manga.fileName || 'Arquivo não especificado'}</span>
        </div>

        <div className="card-meta">
          <div className="meta-item">
            <Calendar size={14} />
            <span>{formattedDate}</span>
          </div>
          {(manga.volume !== undefined && manga.volume !== null) ? (
            <div className="meta-item">
              <Layers size={14} />
              <span>Vol. {manga.volume}</span>
            </div>
          ) : (manga.comicInfo?.volume !== undefined && manga.comicInfo?.volume !== null) ? (
            <div className="meta-item">
              <Layers size={14} />
              <span>Vol. {manga.comicInfo.volume}</span>
            </div>
          ) : null}
          {manga.comicInfo && (
            <div className="meta-item badge-linked" title={`Vinculado: ${manga.comicInfo.series || manga.comicInfo.title}`}>
              <Info size={14} />
              <span>ComicInfo OK</span>
            </div>
          )}
        </div>

        {manga.comicInfo && (
          <div className="card-linked-details">
            <p className="linked-series"><strong>Série:</strong> {manga.comicInfo.series || 'N/A'}</p>
            {manga.comicInfo.publisher && <p className="linked-pub"><strong>Editora:</strong> {manga.comicInfo.publisher}</p>}
          </div>
        )}
      </div>
    </div>
  );
};
