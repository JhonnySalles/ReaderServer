import React from 'react';
import { BookOpen, Calendar, HardDrive, Info } from 'lucide-react';
import type { BookItem } from '../../types/api';
import './Card.css';

interface BookCardProps {
  book: BookItem;
}

export const BookCard: React.FC<BookCardProps> = ({ book }) => {
  const formattedDate = book.fileDate 
    ? new Date(book.fileDate).toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit', year: 'numeric' })
    : 'Sem data';

  return (
    <div className="media-card glass-panel book-card-border">
      <div className="card-badge book-badge">
        <BookOpen size={14} />
        <span>{book.extension ? book.extension.toUpperCase() : 'LIVRO'}</span>
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
