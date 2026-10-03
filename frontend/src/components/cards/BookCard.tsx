import React, { useState } from 'react';
import { BookOpen, Calendar, HardDrive, Info, Download, Loader2 } from 'lucide-react';
import type { BookItem } from '../../types/api';
import { downloadFile } from '../../services/downloadService';
import './Card.css';

interface BookCardProps {
  book: BookItem;
  onClick?: () => void;
}

export const BookCard: React.FC<BookCardProps> = ({ book, onClick }) => {
  const [downloading, setDownloading] = useState(false);
  const formattedDate = book.fileDate 
    ? new Date(book.fileDate).toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit', year: 'numeric' })
    : 'Sem data';

  const handleDownload = async (e: React.MouseEvent) => {
    e.stopPropagation();
    if (!book.id || downloading) return;
    setDownloading(true);
    const cleanName = (book.nome || book.fileName || 'content').replace(/[/\\?%*:|"<>]/g, '_');
    await downloadFile(`/api/book/${book.id}/download`, `${cleanName}.opf`);
    setDownloading(false);
  };

  return (
    <div className="media-card glass-panel book-card-border" onClick={onClick}>
      <div className="card-top-actions">
        {book.opfId && (
          <button
            className="card-download-btn"
            onClick={handleDownload}
            title="Baixar OPF vinculado (XML)"
            disabled={downloading}
          >
            {downloading ? <Loader2 size={14} className="spin-loader" /> : <Download size={14} />}
          </button>
        )}
        <div className="card-badge book-badge">
          <BookOpen size={14} />
          <span>{book.extension ? book.extension.toUpperCase() : 'LIVRO'}</span>
        </div>
      </div>

      <div className="card-body">
        <h3 className="card-title" title={book.nome || book.fileName || 'Sem título'}>
          {book.nome || book.fileName || 'Sem título'}
        </h3>

        <div className="card-filename" title={book.fileName || ''}>
          <HardDrive size={14} className="card-subicon" />
          <span>{book.fileName || 'Arquivo não especificado'}</span>
        </div>

        <div className="card-meta">
          <div className="meta-item">
            <Calendar size={14} />
            <span>{formattedDate}</span>
          </div>
          {book.opf && (
            <div className="meta-item badge-linked-book" title={`Vinculado: ${book.opf.title || book.opf.creator}`}>
              <Info size={14} />
              <span>OPF OK</span>
            </div>
          )}
        </div>

        {book.opf && (
          <div className="card-linked-details">
            <p className="linked-series"><strong>Autor:</strong> {book.opf.creator || 'N/A'}</p>
            {book.opf.publisher && <p className="linked-pub"><strong>Editora:</strong> {book.opf.publisher}</p>}
          </div>
        )}
      </div>
    </div>
  );
};
