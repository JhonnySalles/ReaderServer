import { useState, useEffect, useCallback, useRef } from 'react';
import api from '../services/api';
import type { PageableResponse } from '../types/api';

interface UseInfinitePaginationProps {
  endpoint: string;
  listKey: 'mangaDtoList' | 'bookDtoList' | 'comicInfoDtoList' | 'opfDtoList' | 'dataDtoList';
  searchQuery?: string;
  searchParamName?: string;
  searchEndpoint?: string;
  direction?: 'asc' | 'desc';
  pageSize?: number;
}

export function useInfinitePagination<T>({
  endpoint,
  listKey,
  searchQuery = '',
  searchParamName = 'nome',
  searchEndpoint,
  direction = 'asc',
  pageSize = 20
}: UseInfinitePaginationProps) {
  const [items, setItems] = useState<T[]>([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const observer = useRef<IntersectionObserver | null>(null);

  const fetchItems = useCallback(async (pageToFetch: number, isNewSearch = false) => {
    setLoading(true);
    setError(null);
    try {
      let url = '';
      const params: Record<string, any> = {
        page: pageToFetch,
        size: pageSize,
        direction: direction
      };

      if (searchQuery.trim() !== '') {
        url = searchEndpoint || `${endpoint}/search/${searchParamName}`;
        params[searchParamName] = searchQuery.trim();
      } else {
        url = `${endpoint}/page`;
      }

      const response = await api.get<PageableResponse<T>>(url, { params });
      const data = response.data;
      const fetchedList: T[] = (data._embedded && data._embedded[listKey]) || [];
      const totalPages = data.page?.totalPages || 1;

      setItems(prev => (isNewSearch || pageToFetch === 0) ? fetchedList : [...prev, ...fetchedList]);
      setHasMore(pageToFetch + 1 < totalPages && fetchedList.length > 0);
      setPage(pageToFetch);
    } catch (err: any) {
      console.error(`Erro ao carregar dados de ${endpoint}:`, err);
      setError('Não foi possível carregar os registros da API.');
      if (pageToFetch === 0) setItems([]);
    } finally {
      setLoading(false);
    }
  }, [endpoint, listKey, searchQuery, searchParamName, searchEndpoint, direction, pageSize]);

  // Recarregar quando query ou ordenação mudar
  useEffect(() => {
    setPage(0);
    setHasMore(true);
    fetchItems(0, true);
  }, [searchQuery, direction, fetchItems]);

  const loadMore = useCallback(() => {
    if (!loading && hasMore) {
      fetchItems(page + 1);
    }
  }, [loading, hasMore, page, fetchItems]);

  // Callback de ref para o último elemento (Infinite Scroll)
  const lastElementRef = useCallback((node: HTMLDivElement | null) => {
    if (loading) return;
    if (observer.current) observer.current.disconnect();

    observer.current = new IntersectionObserver(entries => {
      if (entries[0].isIntersecting && hasMore) {
        loadMore();
      }
    });

    if (node) observer.current.observe(node);
  }, [loading, hasMore, loadMore]);

  const refresh = useCallback(() => {
    setPage(0);
    fetchItems(0, true);
  }, [fetchItems]);

  return {
    items,
    loading,
    error,
    hasMore,
    lastElementRef,
    refresh
  };
}
