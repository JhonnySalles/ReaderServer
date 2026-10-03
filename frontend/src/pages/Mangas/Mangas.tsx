import React, { useState } from 'react';
import { Layers, Bookmark, FileText, Calendar, HardDrive, Tag, User, Globe, Hash, Building2, Info } from 'lucide-react';
import { Tabs } from '../../components/ui/Tabs';
import type { TabItem } from '../../components/ui/Tabs';
import { FilterBar } from '../../components/ui/FilterBar';
import { Modal } from '../../components/ui/Modal';
import { MediaGrid } from '../../components/data/MediaGrid';
import { MangaCard } from '../../components/cards/MangaCard';
import { ComicInfoCard } from '../../components/cards/ComicInfoCard';
import { useInfinitePagination } from '../../hooks/useInfinitePagination';
import type { MangaItem, ComicInfoItem } from '../../types/api';
import { downloadFile } from '../../services/downloadService';
import './Mangas.css';

export const Mangas: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'files' | 'comicinfo'>('files');
  const [searchQuery, setSearchQuery] = useState('');
  const [direction, setDirection] = useState<'asc' | 'desc'>('asc');
  const [modalDownloading, setModalDownloading] = useState(false);

  // Estado dos itens selecionados para exibição no modal
  const [selectedManga, setSelectedManga] = useState<MangaItem | null>(null);
  const [selectedComicInfo, setSelectedComicInfo] = useState<ComicInfoItem | null>(null);

  // Consulta da aba de Arquivos Físicos de Mangás
  const mangasPagination = useInfinitePagination<MangaItem>({
    endpoint: '/api/manga',
    listKey: 'mangaDtoList',
    searchQuery: activeTab === 'files' ? searchQuery : '',
    searchParamName: 'nome',
    direction
  });

  // Consulta da aba de Metadados ComicInfo
  const comicInfoPagination = useInfinitePagination<ComicInfoItem>({
    endpoint: '/api/comicinfo',
    listKey: 'comicInfoDtoList',
    searchQuery: activeTab === 'comicinfo' ? searchQuery : '',
    searchParamName: 'title',
    searchEndpoint: '/api/comicinfo/search/title',
    direction
  });

  const tabs: TabItem[] = [
    {
      id: 'files',
      label: 'Arquivos de Mangás',
      icon: <Layers size={18} />
    },
    {
      id: 'comicinfo',
      label: 'Metadados ComicInfo',
      icon: <Bookmark size={18} />
    }
  ];

  const handleDirectionToggle = () => {
    setDirection(prev => (prev === 'asc' ? 'desc' : 'asc'));
  };

  const mangaSuggestions = [
    { command: 'volume', label: 'Volume', example: '@volume:1', description: 'Número do volume' },
    { command: 'series', label: 'Série', example: '@series:Naruto', description: 'Nome da franquia/série' },
    { command: 'author', label: 'Autor / Roteirista', example: '@author:Oda', description: 'Autor ou desenhista' },
    { command: 'publisher', label: 'Editora', example: '@publisher:Panini', description: 'Editora de publicação' },
    { command: 'genre', label: 'Gênero', example: '@genre:Ação', description: 'Gênero da obra' },
    { command: 'type', label: 'Formato / Extensão', example: '@type:cbz', description: 'Extensão do arquivo' }
  ];

  return (
    <div className="mangas-page animate-fade-in">
      <header className="page-header">
        <div>
          <h1 className="page-title gradient-text-manga">Mangás & Comics</h1>
          <p className="page-description">
            Explore arquivos físicos de quadrinhos/mangás e seus respectivos metadados do ComicInfo.xml.
          </p>
        </div>
      </header>

      <Tabs
        tabs={tabs}
        activeTab={activeTab}
        onChange={(id) => {
          setActiveTab(id as 'files' | 'comicinfo');
          setSearchQuery('');
        }}
        accentColor="manga"
      />

      <FilterBar
        searchQuery={searchQuery}
        onSearchChange={setSearchQuery}
        direction={direction}
        onDirectionToggle={handleDirectionToggle}
        suggestions={mangaSuggestions}
        placeholder={activeTab === 'files' ? "Buscar mangá ou use @volume, @series, @author..." : "Buscar ComicInfo ou use @series, @author, @publisher..."}
      />

      {activeTab === 'files' && (
        <MediaGrid
          loading={mangasPagination.loading}
          hasMore={mangasPagination.hasMore}
          lastElementRef={mangasPagination.lastElementRef}
          emptyMessage="Nenhum mangá encontrado para esta busca."
          count={mangasPagination.items.length}
        >
          {mangasPagination.items.map((manga) => (
            <MangaCard 
              key={manga.id} 
              manga={manga} 
              onClick={() => setSelectedManga(manga)} 
            />
          ))}
        </MediaGrid>
      )}

      {activeTab === 'comicinfo' && (
        <MediaGrid
          loading={comicInfoPagination.loading}
          hasMore={comicInfoPagination.hasMore}
          lastElementRef={comicInfoPagination.lastElementRef}
          emptyMessage="Nenhum registro ComicInfo encontrado para esta busca."
          count={comicInfoPagination.items.length}
        >
          {comicInfoPagination.items.map((comicInfo) => (
            <ComicInfoCard 
              key={comicInfo.id} 
              comicInfo={comicInfo} 
              onClick={() => setSelectedComicInfo(comicInfo)} 
            />
          ))}
        </MediaGrid>
      )}

      {/* Modal de Detalhes do Mangá (Arquivo Físico) */}
      <Modal
        isOpen={!!selectedManga}
        onClose={() => setSelectedManga(null)}
        title={selectedManga?.nome || selectedManga?.fileName || 'Detalhes do Arquivo de Mangá'}
        icon={<Layers size={22} color="var(--color-primary)" />}
        onDownload={selectedManga?.comicInfoId ? async () => {
          if (!selectedManga?.id || modalDownloading) return;
          setModalDownloading(true);
          const cleanName = (selectedManga.nome || selectedManga.fileName || 'ComicInfo').replace(/[/\\?%*:|"<>]/g, '_');
          await downloadFile(`/api/manga/${selectedManga.id}/download`, `${cleanName}.xml`);
          setModalDownloading(false);
        } : undefined}
        downloadLabel="Baixar ComicInfo"
        downloading={modalDownloading}
      >
        {selectedManga && (
          <>
            <div className="modal-detail-section">
              <span className="modal-section-title">Dados do Arquivo</span>
              <div className="modal-grid-fields">
                <div className="modal-field">
                  <span className="modal-field-label"><FileText size={14} /> Nome</span>
                  <span className="modal-field-value">{selectedManga.nome || 'N/A'}</span>
                </div>
                <div className="modal-field">
                  <span className="modal-field-label"><HardDrive size={14} /> Nome do Arquivo</span>
                  <span className="modal-field-value">{selectedManga.fileName || 'N/A'}</span>
                </div>
                <div className="modal-field">
                  <span className="modal-field-label"><Layers size={14} /> Extensão</span>
                  <span className="modal-field-value">{selectedManga.extension ? selectedManga.extension.toUpperCase() : 'N/A'}</span>
                </div>
                <div className="modal-field">
                  <span className="modal-field-label"><Calendar size={14} /> Data do Arquivo</span>
                  <span className="modal-field-value">
                    {selectedManga.fileDate ? new Date(selectedManga.fileDate).toLocaleString('pt-BR') : 'Sem data'}
                  </span>
                </div>
                <div className="modal-field">
                  <span className="modal-field-label"><Hash size={14} /> ID do Registro</span>
                  <span className="modal-field-value">{selectedManga.id || 'N/A'}</span>
                </div>
              </div>
            </div>

            {selectedManga.comicInfo && (
              <div className="modal-detail-section">
                <span className="modal-section-title">Metadados Vinculados (ComicInfo)</span>
                <div className="modal-grid-fields">
                  <div className="modal-field">
                    <span className="modal-field-label"><Bookmark size={14} /> Série</span>
                    <span className="modal-field-value">{selectedManga.comicInfo.series || 'N/A'}</span>
                  </div>
                  <div className="modal-field">
                    <span className="modal-field-label"><FileText size={14} /> Título</span>
                    <span className="modal-field-value">{selectedManga.comicInfo.title || 'N/A'}</span>
                  </div>
                  <div className="modal-field">
                    <span className="modal-field-label"><Hash size={14} /> Edição / Volume</span>
                    <span className="modal-field-value">
                      {selectedManga.comicInfo.number ? `Nº ${selectedManga.comicInfo.number}` : ''} 
                      {selectedManga.comicInfo.volume ? ` (Vol. ${selectedManga.comicInfo.volume})` : ''}
                      {!selectedManga.comicInfo.number && !selectedManga.comicInfo.volume ? 'N/A' : ''}
                    </span>
                  </div>
                  <div className="modal-field">
                    <span className="modal-field-label"><User size={14} /> Roteirista (Writer)</span>
                    <span className="modal-field-value">{selectedManga.comicInfo.writer || 'N/A'}</span>
                  </div>
                  <div className="modal-field">
                    <span className="modal-field-label"><Building2 size={14} /> Editora (Publisher)</span>
                    <span className="modal-field-value">{selectedManga.comicInfo.publisher || 'N/A'}</span>
                  </div>
                  <div className="modal-field">
                    <span className="modal-field-label"><Globe size={14} /> Idioma</span>
                    <span className="modal-field-value">{selectedManga.comicInfo.languageISO?.toUpperCase() || 'N/A'}</span>
                  </div>
                </div>

                {selectedManga.comicInfo.genre && (
                  <div className="modal-detail-section" style={{ marginTop: '8px' }}>
                    <span className="modal-field-label"><Tag size={14} /> Gêneros</span>
                    <div className="modal-tags-container">
                      {selectedManga.comicInfo.genre.split(/[,;/]+/).map((tag, idx) => (
                        <span key={idx} className="modal-tag">{tag.trim()}</span>
                      ))}
                    </div>
                  </div>
                )}

                {selectedManga.comicInfo.summary && (
                  <div className="modal-detail-section" style={{ marginTop: '8px' }}>
                    <span className="modal-field-label"><Info size={14} /> Sinopse</span>
                    <div className="modal-text-block">{selectedManga.comicInfo.summary}</div>
                  </div>
                )}
              </div>
            )}
          </>
        )}
      </Modal>

      {/* Modal de Detalhes do Metadado ComicInfo */}
      <Modal
        isOpen={!!selectedComicInfo}
        onClose={() => setSelectedComicInfo(null)}
        title={selectedComicInfo?.title || selectedComicInfo?.series || 'Detalhes do ComicInfo'}
        icon={<Bookmark size={22} color="#c084fc" />}
        onDownload={async () => {
          if (!selectedComicInfo?.id || modalDownloading) return;
          setModalDownloading(true);
          const cleanName = (selectedComicInfo.series || selectedComicInfo.title || 'ComicInfo').replace(/[/\\?%*:|"<>]/g, '_');
          await downloadFile(`/api/comicinfo/${selectedComicInfo.id}/download`, `${cleanName}.xml`);
          setModalDownloading(false);
        }}
        downloadLabel="Baixar ComicInfo.xml"
        downloading={modalDownloading}
      >
        {selectedComicInfo && (
          <>
            <div className="modal-detail-section">
              <span className="modal-section-title">Informações Principais</span>
              <div className="modal-grid-fields">
                <div className="modal-field">
                  <span className="modal-field-label"><FileText size={14} /> Título</span>
                  <span className="modal-field-value">{selectedComicInfo.title || 'N/A'}</span>
                </div>
                <div className="modal-field">
                  <span className="modal-field-label"><Bookmark size={14} /> Série</span>
                  <span className="modal-field-value">{selectedComicInfo.series || 'N/A'}</span>
                </div>
                <div className="modal-field">
                  <span className="modal-field-label"><Hash size={14} /> Número / Edição</span>
                  <span className="modal-field-value">{selectedComicInfo.number ?? 'N/A'}</span>
                </div>
                <div className="modal-field">
                  <span className="modal-field-label"><Layers size={14} /> Volume</span>
                  <span className="modal-field-value">{selectedComicInfo.volume ?? 'N/A'}</span>
                </div>
                <div className="modal-field">
                  <span className="modal-field-label"><Building2 size={14} /> Editora (Publisher)</span>
                  <span className="modal-field-value">{selectedComicInfo.publisher || 'N/A'}</span>
                </div>
                <div className="modal-field">
                  <span className="modal-field-label"><User size={14} /> Roteirista (Writer)</span>
                  <span className="modal-field-value">{selectedComicInfo.writer || 'N/A'}</span>
                </div>
                <div className="modal-field">
                  <span className="modal-field-label"><User size={14} /> Ilustrador (Penciller)</span>
                  <span className="modal-field-value">{selectedComicInfo.penciller || 'N/A'}</span>
                </div>
                <div className="modal-field">
                  <span className="modal-field-label"><Calendar size={14} /> Publicação</span>
                  <span className="modal-field-value">
                    {[selectedComicInfo.day, selectedComicInfo.month, selectedComicInfo.year].filter(Boolean).join('/') || 'N/A'}
                  </span>
                </div>
                <div className="modal-field">
                  <span className="modal-field-label"><Globe size={14} /> Idioma (ISO)</span>
                  <span className="modal-field-value">{selectedComicInfo.languageISO?.toUpperCase() || 'N/A'}</span>
                </div>
                <div className="modal-field">
                  <span className="modal-field-label"><Info size={14} /> Classificação Etária</span>
                  <span className="modal-field-value">{selectedComicInfo.ageRating || 'Livre / N/A'}</span>
                </div>
                {selectedComicInfo.idMal && (
                  <div className="modal-field">
                    <span className="modal-field-label"><Hash size={14} /> ID MyAnimeList</span>
                    <span className="modal-field-value">{selectedComicInfo.idMal}</span>
                  </div>
                )}
              </div>
            </div>

            {selectedComicInfo.genre && (
              <div className="modal-detail-section">
                <span className="modal-section-title">Gêneros / Tags</span>
                <div className="modal-tags-container">
                  {selectedComicInfo.genre.split(/[,;/]+/).map((genre, idx) => (
                    <span key={idx} className="modal-tag">
                      <Tag size={12} /> {genre.trim()}
                    </span>
                  ))}
                </div>
              </div>
            )}

            {selectedComicInfo.summary && (
              <div className="modal-detail-section">
                <span className="modal-section-title">Sinopse</span>
                <div className="modal-text-block">{selectedComicInfo.summary}</div>
              </div>
            )}

            {selectedComicInfo.notes && (
              <div className="modal-detail-section">
                <span className="modal-section-title">Notas</span>
                <div className="modal-text-block">{selectedComicInfo.notes}</div>
              </div>
            )}
          </>
        )}
      </Modal>
    </div>
  );
};

