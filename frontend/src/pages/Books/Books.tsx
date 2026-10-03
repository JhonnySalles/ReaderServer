import React, { useState } from 'react';
import { BookOpen, FileCode2, FileText, Calendar, HardDrive, Tag, User, Globe, Hash, Building2, Info, Bookmark } from 'lucide-react';
import { Tabs } from '../../components/ui/Tabs';
import type { TabItem } from '../../components/ui/Tabs';
import { FilterBar } from '../../components/ui/FilterBar';
import { Modal } from '../../components/ui/Modal';
import { MediaGrid } from '../../components/data/MediaGrid';
import { BookCard } from '../../components/cards/BookCard';
import { OpfCard } from '../../components/cards/OpfCard';
import { useInfinitePagination } from '../../hooks/useInfinitePagination';
import type { BookItem, OpfItem } from '../../types/api';
import { downloadFile } from '../../services/downloadService';
import './Books.css';

export const Books: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'files' | 'opf'>('files');
  const [searchQuery, setSearchQuery] = useState('');
  const [direction, setDirection] = useState<'asc' | 'desc'>('asc');
  const [modalDownloading, setModalDownloading] = useState(false);

  // Estado dos itens selecionados para o modal
  const [selectedBook, setSelectedBook] = useState<BookItem | null>(null);
  const [selectedOpf, setSelectedOpf] = useState<OpfItem | null>(null);

  // Consulta da aba de Arquivos Físicos de Livros
  const booksPagination = useInfinitePagination<BookItem>({
    endpoint: '/api/book',
    listKey: 'bookDtoList',
    searchQuery: activeTab === 'files' ? searchQuery : '',
    searchParamName: 'nome',
    direction
  });

  // Consulta da aba de Metadados OPF
  const opfPagination = useInfinitePagination<OpfItem>({
    endpoint: '/api/opf',
    listKey: 'opfDtoList',
    searchQuery: activeTab === 'opf' ? searchQuery : '',
    searchParamName: 'title',
    searchEndpoint: '/api/opf/search/title',
    direction
  });

  const tabs: TabItem[] = [
    {
      id: 'files',
      label: 'Arquivos de Livros',
      icon: <BookOpen size={18} />
    },
    {
      id: 'opf',
      label: 'Metadados OPF',
      icon: <FileCode2 size={18} />
    }
  ];

  const handleDirectionToggle = () => {
    setDirection(prev => (prev === 'asc' ? 'desc' : 'asc'));
  };

  const bookSuggestions = [
    { command: 'volume', label: 'Volume', example: '@volume:1', description: 'Número do volume' },
    { command: 'series', label: 'Série', example: '@series:Duna', description: 'Saga ou série do livro' },
    { command: 'creator', label: 'Autor / Criador', example: '@creator:Tolkien', description: 'Autor ou criador da obra' },
    { command: 'publisher', label: 'Editora', example: '@publisher:Rocco', description: 'Editora de publicação' },
    { command: 'language', label: 'Idioma', example: '@language:pt', description: 'Código do idioma (ex: pt, en)' },
    { command: 'subjects', label: 'Assunto / Tags', example: '@subjects:Ficção', description: 'Gênero ou assunto' },
    { command: 'type', label: 'Formato / Extensão', example: '@type:epub', description: 'Extensão do arquivo' }
  ];

  return (
    <div className="books-page animate-fade-in">
      <header className="page-header">
        <div>
          <h1 className="page-title gradient-text-book">Livros & Epubs</h1>
          <p className="page-description">
            Explore arquivos de livros físicos cadastrados e seus respectivos metadados OPF.
          </p>
        </div>
      </header>

      <Tabs
        tabs={tabs}
        activeTab={activeTab}
        onChange={(id) => {
          setActiveTab(id as 'files' | 'opf');
          setSearchQuery('');
        }}
        accentColor="book"
      />

      <FilterBar
        searchQuery={searchQuery}
        onSearchChange={setSearchQuery}
        direction={direction}
        onDirectionToggle={handleDirectionToggle}
        suggestions={bookSuggestions}
        placeholder={activeTab === 'files' ? "Buscar livro ou use @volume, @series, @creator..." : "Buscar metadado OPF ou use @creator, @series, @publisher..."}
      />

      {activeTab === 'files' && (
        <MediaGrid
          loading={booksPagination.loading}
          hasMore={booksPagination.hasMore}
          lastElementRef={booksPagination.lastElementRef}
          emptyMessage="Nenhum livro encontrado para esta busca."
          count={booksPagination.items.length}
        >
          {booksPagination.items.map((book) => (
            <BookCard 
              key={book.id} 
              book={book} 
              onClick={() => setSelectedBook(book)} 
            />
          ))}
        </MediaGrid>
      )}

      {activeTab === 'opf' && (
        <MediaGrid
          loading={opfPagination.loading}
          hasMore={opfPagination.hasMore}
          lastElementRef={opfPagination.lastElementRef}
          emptyMessage="Nenhum registro OPF encontrado para esta busca."
          count={opfPagination.items.length}
        >
          {opfPagination.items.map((opf) => (
            <OpfCard 
              key={opf.id} 
              opf={opf} 
              onClick={() => setSelectedOpf(opf)} 
            />
          ))}
        </MediaGrid>
      )}

      {/* Modal de Detalhes do Livro (Arquivo Físico) */}
      <Modal
        isOpen={!!selectedBook}
        onClose={() => setSelectedBook(null)}
        title={selectedBook?.nome || selectedBook?.fileName || 'Detalhes do Arquivo de Livro'}
        icon={<BookOpen size={22} color="#fbbf24" />}
        onDownload={selectedBook?.opfId ? async () => {
          if (!selectedBook?.id || modalDownloading) return;
          setModalDownloading(true);
          const cleanName = (selectedBook.nome || selectedBook.fileName || 'content').replace(/[/\\?%*:|"<>]/g, '_');
          await downloadFile(`/api/book/${selectedBook.id}/download`, `${cleanName}.opf`);
          setModalDownloading(false);
        } : undefined}
        downloadLabel="Baixar OPF"
        downloading={modalDownloading}
      >
        {selectedBook && (
          <>
            <div className="modal-detail-section">
              <span className="modal-section-title">Dados do Arquivo</span>
              <div className="modal-grid-fields">
                <div className="modal-field">
                  <span className="modal-field-label"><FileText size={14} /> Nome</span>
                  <span className="modal-field-value">{selectedBook.nome || 'N/A'}</span>
                </div>
                <div className="modal-field">
                  <span className="modal-field-label"><HardDrive size={14} /> Nome do Arquivo</span>
                  <span className="modal-field-value">{selectedBook.fileName || 'N/A'}</span>
                </div>
                <div className="modal-field">
                  <span className="modal-field-label"><BookOpen size={14} /> Extensão</span>
                  <span className="modal-field-value">{selectedBook.extension ? selectedBook.extension.toUpperCase() : 'N/A'}</span>
                </div>
                <div className="modal-field">
                  <span className="modal-field-label"><Calendar size={14} /> Data do Arquivo</span>
                  <span className="modal-field-value">
                    {selectedBook.fileDate ? new Date(selectedBook.fileDate).toLocaleString('pt-BR') : 'Sem data'}
                  </span>
                </div>
                <div className="modal-field">
                  <span className="modal-field-label"><Hash size={14} /> ID do Registro</span>
                  <span className="modal-field-value">{selectedBook.id || 'N/A'}</span>
                </div>
              </div>
            </div>

            {selectedBook.opf && (
              <div className="modal-detail-section">
                <span className="modal-section-title">Metadados Vinculados (OPF)</span>
                <div className="modal-grid-fields">
                  <div className="modal-field">
                    <span className="modal-field-label"><FileText size={14} /> Título</span>
                    <span className="modal-field-value">{selectedBook.opf.title || 'N/A'}</span>
                  </div>
                  <div className="modal-field">
                    <span className="modal-field-label"><User size={14} /> Autor (Creator)</span>
                    <span className="modal-field-value">{selectedBook.opf.creator || 'N/A'}</span>
                  </div>
                  <div className="modal-field">
                    <span className="modal-field-label"><Building2 size={14} /> Editora</span>
                    <span className="modal-field-value">{selectedBook.opf.publisher || 'N/A'}</span>
                  </div>
                  <div className="modal-field">
                    <span className="modal-field-label"><Calendar size={14} /> Publicação</span>
                    <span className="modal-field-value">{selectedBook.opf.datePublished || 'N/A'}</span>
                  </div>
                  <div className="modal-field">
                    <span className="modal-field-label"><Globe size={14} /> Idioma</span>
                    <span className="modal-field-value">{selectedBook.opf.language?.toUpperCase() || 'N/A'}</span>
                  </div>
                  {selectedBook.opf.series && (
                    <div className="modal-field">
                      <span className="modal-field-label"><Bookmark size={14} /> Série</span>
                      <span className="modal-field-value">
                        {selectedBook.opf.series} {selectedBook.opf.seriesIndex ? `(#${selectedBook.opf.seriesIndex})` : ''}
                      </span>
                    </div>
                  )}
                </div>

                {selectedBook.opf.subjects && (
                  <div className="modal-detail-section" style={{ marginTop: '8px' }}>
                    <span className="modal-field-label"><Tag size={14} /> Assuntos / Tags</span>
                    <div className="modal-tags-container">
                      {selectedBook.opf.subjects.split(/[,;/]+/).map((subject, idx) => (
                        <span key={idx} className="modal-tag">{subject.trim()}</span>
                      ))}
                    </div>
                  </div>
                )}

                {selectedBook.opf.description && (
                  <div className="modal-detail-section" style={{ marginTop: '8px' }}>
                    <span className="modal-field-label"><Info size={14} /> Descrição / Sinopse</span>
                    <div className="modal-text-block">{selectedBook.opf.description}</div>
                  </div>
                )}
              </div>
            )}
          </>
        )}
      </Modal>

      {/* Modal de Detalhes do Metadado OPF */}
      <Modal
        isOpen={!!selectedOpf}
        onClose={() => setSelectedOpf(null)}
        title={selectedOpf?.title || 'Detalhes do Metadado OPF'}
        icon={<FileCode2 size={22} color="#f472b6" />}
        onDownload={async () => {
          if (!selectedOpf?.id || modalDownloading) return;
          setModalDownloading(true);
          const cleanName = (selectedOpf.title || 'content').replace(/[/\\?%*:|"<>]/g, '_');
          await downloadFile(`/api/opf/${selectedOpf.id}/download`, `${cleanName}.opf`);
          setModalDownloading(false);
        }}
        downloadLabel="Baixar OPF XML"
        downloading={modalDownloading}
      >
        {selectedOpf && (
          <>
            <div className="modal-detail-section">
              <span className="modal-section-title">Informações do Livro</span>
              <div className="modal-grid-fields">
                <div className="modal-field">
                  <span className="modal-field-label"><FileText size={14} /> Título</span>
                  <span className="modal-field-value">{selectedOpf.title || 'N/A'}</span>
                </div>
                <div className="modal-field">
                  <span className="modal-field-label"><User size={14} /> Autor (Creator)</span>
                  <span className="modal-field-value">{selectedOpf.creator || 'N/A'}</span>
                </div>
                {selectedOpf.contributor && (
                  <div className="modal-field">
                    <span className="modal-field-label"><User size={14} /> Contribuidor</span>
                    <span className="modal-field-value">{selectedOpf.contributor}</span>
                  </div>
                )}
                <div className="modal-field">
                  <span className="modal-field-label"><Building2 size={14} /> Editora (Publisher)</span>
                  <span className="modal-field-value">{selectedOpf.publisher || 'N/A'}</span>
                </div>
                <div className="modal-field">
                  <span className="modal-field-label"><Calendar size={14} /> Data de Publicação</span>
                  <span className="modal-field-value">{selectedOpf.datePublished || 'N/A'}</span>
                </div>
                <div className="modal-field">
                  <span className="modal-field-label"><Globe size={14} /> Idioma</span>
                  <span className="modal-field-value">{selectedOpf.language?.toUpperCase() || 'N/A'}</span>
                </div>
                {selectedOpf.series && (
                  <div className="modal-field">
                    <span className="modal-field-label"><Bookmark size={14} /> Série</span>
                    <span className="modal-field-value">
                      {selectedOpf.series} {selectedOpf.seriesIndex ? `(#${selectedOpf.seriesIndex})` : ''}
                    </span>
                  </div>
                )}
                {selectedOpf.identifiers && (
                  <div className="modal-field">
                    <span className="modal-field-label"><Hash size={14} /> Identificadores (ISBN/ID)</span>
                    <span className="modal-field-value">{selectedOpf.identifiers}</span>
                  </div>
                )}
                {selectedOpf.rights && (
                  <div className="modal-field">
                    <span className="modal-field-label"><Info size={14} /> Direitos (Rights)</span>
                    <span className="modal-field-value">{selectedOpf.rights}</span>
                  </div>
                )}
              </div>
            </div>

            {selectedOpf.subjects && (
              <div className="modal-detail-section">
                <span className="modal-section-title">Assuntos / Tags</span>
                <div className="modal-tags-container">
                  {selectedOpf.subjects.split(/[,;/]+/).map((subject, idx) => (
                    <span key={idx} className="modal-tag">
                      <Tag size={12} /> {subject.trim()}
                    </span>
                  ))}
                </div>
              </div>
            )}

            {selectedOpf.description && (
              <div className="modal-detail-section">
                <span className="modal-section-title">Descrição / Sinopse</span>
                <div className="modal-text-block">{selectedOpf.description}</div>
              </div>
            )}
          </>
        )}
      </Modal>
    </div>
  );
};

