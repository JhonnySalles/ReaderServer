import React, { useState } from 'react';
import { Tag, Bookmark, User, Globe, Calendar, Download, Loader2 } from 'lucide-react';
import type { ComicInfoItem } from '../../types/api';
import { downloadFile } from '../../services/downloadService';
import './Card.css';

interface ComicInfoCardProps {
  comicInfo: ComicInfoItem;
  onClick?: () => void;
}

export const ComicInfoCard: React.FC<ComicInfoCardProps> = ({ comicInfo, onClick }) => {
  const [downloading, setDownloading] = useState(false);

  const handleDownload = async (e: React.MouseEvent) => {
    e.stopPropagation();
    if (!comicInfo.id || downloading) return;
    setDownloading(true);
    const cleanName = (comicInfo.series || comicInfo.title || 'ComicInfo').replace(/[/\\?%*:|"<>]/g, '_');
    await downloadFile(`/api/comicinfo/${comicInfo.id}/download`, `${cleanName}.xml`);
    setDownloading(false);
  };

  return (
    <div className="media-card glass-panel comicinfo-card-border" onClick={onClick}>
      <div className="card-top-actions">
        <button
          className="card-download-btn"
          onClick={handleDownload}
          title="Baixar ComicInfo.xml"
          disabled={downloading}
        >
          {downloading ? <Loader2 size={14} className="spin-loader" /> : <Download size={14} />}
        </button>
        <div className="card-badge comicinfo-badge">
          <Bookmark size={14} />
          <span>COMICINFO</span>
        </div>
      </div>

      <div className="card-body">
        <h3 className="card-title" title={comicInfo.title || comicInfo.series || 'Sem título'}>
          {comicInfo.title || comicInfo.series || 'Sem título'}
        </h3>

        <div className="card-filename">
          <Bookmark size={14} className="card-subicon" />
          <span>Série: {comicInfo.series || 'N/A'} {comicInfo.number ? `#${comicInfo.number}` : ''}</span>
        </div>

        <div className="card-meta">
          {comicInfo.writer && (
            <div className="meta-item">
              <User size={14} />
              <span>{comicInfo.writer}</span>
            </div>
          )}
          {comicInfo.year && (
            <div className="meta-item">
              <Calendar size={14} />
              <span>{comicInfo.year}</span>
            </div>
          )}
          {comicInfo.languageISO && (
            <div className="meta-item">
              <Globe size={14} />
              <span>{comicInfo.languageISO.toUpperCase()}</span>
            </div>
          )}
        </div>

        {comicInfo.genre && (
          <div className="card-tags">
            <Tag size={12} />
            <span>{comicInfo.genre}</span>
          </div>
        )}
      </div>
    </div>
  );
};
