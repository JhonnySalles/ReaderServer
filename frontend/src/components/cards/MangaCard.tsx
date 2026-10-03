import React from 'react';
import { Layers, Calendar, HardDrive, Info } from 'lucide-react';
import type { MangaItem } from '../../types/api';
import './Card.css';

interface MangaCardProps {
  manga: MangaItem;
}

export const MangaCard: React.FC<MangaCardProps> = ({ manga }) => {
  const formattedDate = manga.fileDate 
    ? new Date(manga.fileDate).toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit', year: 'numeric' })
    : 'Sem data';

  return (
    <div className="media-card glass-panel manga-card-border">
      <div className="card-badge manga-badge">
        <Layers size={14} />
        <span>{manga.extension ? manga.extension.toUpperCase() : 'MANGA'}</span>
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
