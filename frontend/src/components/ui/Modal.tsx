import React, { useEffect } from 'react';
import { createPortal } from 'react-dom';
import { X, Download, Loader2 } from 'lucide-react';
import './Modal.css';

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  title: string;
  icon?: React.ReactNode;
  children: React.ReactNode;
  maxWidth?: string;
  onDownload?: () => void;
  downloadLabel?: string;
  downloading?: boolean;
}

export const Modal: React.FC<ModalProps> = ({
  isOpen,
  onClose,
  title,
  icon,
  children,
  maxWidth,
  onDownload,
  downloadLabel = "Baixar",
  downloading = false
}) => {
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        onClose();
      }
    };

    if (isOpen) {
      document.body.style.overflow = 'hidden';
      window.addEventListener('keydown', handleKeyDown);
    }

    return () => {
      document.body.style.overflow = '';
      window.removeEventListener('keydown', handleKeyDown);
    };
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  return createPortal(
    <div className="modal-backdrop" onClick={onClose} role="dialog" aria-modal="true">
      <div 
        className="modal-container" 
        style={maxWidth ? { maxWidth } : undefined}
        onClick={(e) => e.stopPropagation()}
      >
        <div className="modal-header">
          <div className="modal-title-group">
            {icon && <span className="modal-icon">{icon}</span>}
            <h2 className="modal-title" title={title}>{title}</h2>
          </div>

          <div className="modal-header-actions">
            {onDownload && (
              <button 
                type="button" 
                className="modal-download-btn" 
                onClick={onDownload} 
                disabled={downloading}
                title={downloadLabel}
              >
                {downloading ? <Loader2 size={14} className="spin-loader" /> : <Download size={14} />}
                <span>{downloading ? 'Baixando...' : downloadLabel}</span>
              </button>
            )}
            <button 
              type="button" 
              className="modal-close-btn" 
              onClick={onClose} 
              aria-label="Fechar modal"
            >
              <X size={20} />
            </button>
          </div>
        </div>

        <div className="modal-body">
          {children}
        </div>
      </div>
    </div>,
    document.body
  );
};
