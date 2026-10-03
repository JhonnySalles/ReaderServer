import React, { useState } from 'react';
import { BookOpen, FileCode2 } from 'lucide-react';
import { Tabs } from '../../components/ui/Tabs';
import type { TabItem } from '../../components/ui/Tabs';
import { FilterBar } from '../../components/ui/FilterBar';
import { MediaGrid } from '../../components/data/MediaGrid';
import { BookCard } from '../../components/cards/BookCard';
import { OpfCard } from '../../components/cards/OpfCard';
import { useInfinitePagination } from '../../hooks/useInfinitePagination';
import type { BookItem, OpfItem } from '../../types/api';
import './Books.css';

export const Books: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'files' | 'opf'>('files');
  const [searchQuery, setSearchQuery] = useState('');
  const [direction, setDirection] = useState<'asc' | 'desc'>('asc');

  // Consulta da aba de Arquivos Físicos de Livros
  const booksPagination = useInfinitePagination<BookItem>({
    endpoint: '/book',
    listKey: 'bookDtoList',
    searchQuery: activeTab === 'files' ? searchQuery : '',
    searchParamName: 'nome',
    direction
  });

  // Consulta da aba de Metadados OPF
  const opfPagination = useInfinitePagination<OpfItem>({
    endpoint: '/opf',
    listKey: 'opfDtoList',
    searchQuery: activeTab === 'opf' ? searchQuery : '',
    searchParamName: 'title',
    searchEndpoint: '/opf/search/title',
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
        placeholder={activeTab === 'files' ? "Buscar livro por nome..." : "Buscar metadado OPF por título..."}
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
            <BookCard key={book.id} book={book} />
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
            <OpfCard key={opf.id} opf={opf} />
          ))}
        </MediaGrid>
      )}
    </div>
  );
};
