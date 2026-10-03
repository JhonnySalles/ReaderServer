import React from 'react';
import { Tag, Book, User, Globe, Calendar } from 'lucide-react';
import type { OpfItem } from '../../types/api';
import './Card.css';

interface OpfCardProps {
  opf: OpfItem;
}

export const OpfCard: React.FC<OpfCardProps> = ({ opf }) => {
  return (
    <div className="media-card glass-panel opf-card-border">
      <div className="card-badge opf-badge">
        <Book size={14} />
        <span>OPF METADATA</span>
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
