import React from 'react';
import { Tag, Bookmark, User, Globe, Calendar } from 'lucide-react';
import type { ComicInfoItem } from '../../types/api';
import './Card.css';

interface ComicInfoCardProps {
  comicInfo: ComicInfoItem;
}

export const ComicInfoCard: React.FC<ComicInfoCardProps> = ({ comicInfo }) => {
  return (
    <div className="media-card glass-panel comicinfo-card-border">
      <div className="card-badge comicinfo-badge">
        <Bookmark size={14} />
        <span>COMICINFO</span>
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
