import React, { useState } from 'react';
import { Layers, Bookmark } from 'lucide-react';
import { Tabs } from '../../components/ui/Tabs';
import type { TabItem } from '../../components/ui/Tabs';
import { FilterBar } from '../../components/ui/FilterBar';
import { MediaGrid } from '../../components/data/MediaGrid';
import { MangaCard } from '../../components/cards/MangaCard';
import { ComicInfoCard } from '../../components/cards/ComicInfoCard';
import { useInfinitePagination } from '../../hooks/useInfinitePagination';
import type { MangaItem, ComicInfoItem } from '../../types/api';
import './Mangas.css';

export const Mangas: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'files' | 'comicinfo'>('files');
  const [searchQuery, setSearchQuery] = useState('');
  const [direction, setDirection] = useState<'asc' | 'desc'>('asc');

  // Consulta da aba de Arquivos Físicos de Mangás
  const mangasPagination = useInfinitePagination<MangaItem>({
    endpoint: '/manga',
    listKey: 'mangaDtoList',
    searchQuery: activeTab === 'files' ? searchQuery : '',
    searchParamName: 'nome',
    direction
  });

  // Consulta da aba de Metadados ComicInfo
  const comicInfoPagination = useInfinitePagination<ComicInfoItem>({
    endpoint: '/comicinfo',
    listKey: 'comicInfoDtoList',
    searchQuery: activeTab === 'comicinfo' ? searchQuery : '',
    searchParamName: 'title',
    searchEndpoint: '/comicinfo/search/title',
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
        placeholder={activeTab === 'files' ? "Buscar mangá por nome..." : "Buscar metadado por título da série/comic..."}
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
            <MangaCard key={manga.id} manga={manga} />
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
            <ComicInfoCard key={comicInfo.id} comicInfo={comicInfo} />
          ))}
        </MediaGrid>
      )}
    </div>
  );
};
