import React, { useState, useRef, useEffect } from 'react';
import { Search, ArrowUpDown, X, Sparkles } from 'lucide-react';
import './FilterBar.css';

export interface SearchSuggestion {
  command: string;
  label: string;
  example: string;
  description: string;
}

interface FilterBarProps {
  searchQuery: string;
  onSearchChange: (value: string) => void;
  direction: 'asc' | 'desc';
  onDirectionToggle: () => void;
  placeholder?: string;
  suggestions?: SearchSuggestion[];
}

export const FilterBar: React.FC<FilterBarProps> = ({
  searchQuery,
  onSearchChange,
  direction,
  onDirectionToggle,
  placeholder = "Filtrar por nome ou use @ para comandos...",
  suggestions = []
}) => {
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [suggestionFilter, setSuggestionFilter] = useState('');
  const [selectedIndex, setSelectedIndex] = useState(0);
  const inputRef = useRef<HTMLInputElement>(null);
  const wrapperRef = useRef<HTMLDivElement>(null);

  // Monitora digitação para ativar o dropdown de sugestões ao encontrar '@'
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const val = e.target.value;
    onSearchChange(val);

    const cursorPos = e.target.selectionStart || val.length;
    const textBeforeCursor = val.slice(0, cursorPos);
    const lastAtPos = textBeforeCursor.lastIndexOf('@');

    if (lastAtPos !== -1) {
      const queryAfterAt = textBeforeCursor.slice(lastAtPos + 1);
      // Se não há espaços ou dois pontos após o @, estamos digitando a tag
      if (!queryAfterAt.includes(' ') && !queryAfterAt.includes(':')) {
        setSuggestionFilter(queryAfterAt.toLowerCase());
        setShowSuggestions(true);
        setSelectedIndex(0);
        return;
      }
    }
    setShowSuggestions(false);
  };

  const filteredSuggestions = suggestions.filter(s =>
    s.command.toLowerCase().includes(suggestionFilter) ||
    s.label.toLowerCase().includes(suggestionFilter)
  );

  const applySuggestion = (suggestion: SearchSuggestion) => {
    if (!inputRef.current) return;
    const val = searchQuery;
    const cursorPos = inputRef.current.selectionStart || val.length;
    const textBeforeCursor = val.slice(0, cursorPos);
    const textAfterCursor = val.slice(cursorPos);
    const lastAtPos = textBeforeCursor.lastIndexOf('@');

    if (lastAtPos !== -1) {
      const prefix = textBeforeCursor.slice(0, lastAtPos);
      const inserted = `@${suggestion.command}:`;
      const newVal = `${prefix}${inserted}${textAfterCursor}`.replace(/\s+/g, ' ');
      onSearchChange(newVal);

      setShowSuggestions(false);
      setTimeout(() => {
        if (inputRef.current) {
          const newPos = prefix.length + inserted.length;
          inputRef.current.focus();
          inputRef.current.setSelectionRange(newPos, newPos);
        }
      }, 50);
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (showSuggestions && filteredSuggestions.length > 0) {
      if (e.key === 'ArrowDown') {
        e.preventDefault();
        setSelectedIndex(prev => (prev + 1) % filteredSuggestions.length);
      } else if (e.key === 'ArrowUp') {
        e.preventDefault();
        setSelectedIndex(prev => (prev - 1 + filteredSuggestions.length) % filteredSuggestions.length);
      } else if (e.key === 'Enter' || e.key === 'Tab') {
        e.preventDefault();
        applySuggestion(filteredSuggestions[selectedIndex]);
      } else if (e.key === 'Escape') {
        setShowSuggestions(false);
      }
    }
  };

  // Fecha dropdown ao clicar fora
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (wrapperRef.current && !wrapperRef.current.contains(event.target as Node)) {
        setShowSuggestions(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const isDropdownOpen = showSuggestions && filteredSuggestions.length > 0;

  return (
    <div className="filter-bar glass-panel" ref={wrapperRef}>
      <div className={`search-input-wrapper ${isDropdownOpen ? 'dropdown-open' : ''}`}>
        <Search size={18} className="search-icon" />
        <input
          ref={inputRef}
          type="text"
          className="search-input"
          placeholder={placeholder}
          value={searchQuery}
          onChange={handleInputChange}
          onKeyDown={handleKeyDown}
          onFocus={() => {
            if (searchQuery.endsWith('@')) setShowSuggestions(true);
          }}
        />
        {searchQuery && (
          <button 
            className="clear-search-btn" 
            onClick={() => {
              onSearchChange('');
              setShowSuggestions(false);
            }}
            title="Limpar busca"
          >
            <X size={16} />
          </button>
        )}

        {/* Dropdown com sugestões ao digitar @ */}
        {isDropdownOpen && (
          <div className="search-suggestions-dropdown animate-fade-in">
            <div className="suggestions-header">
              <Sparkles size={14} className="suggestions-icon" />
              <span>Filtros Inteligentes (Comandos)</span>
            </div>
            <ul className="suggestions-list">
              {filteredSuggestions.map((s, idx) => (
                <li
                  key={s.command}
                  className={`suggestion-item ${idx === selectedIndex ? 'active' : ''}`}
                  onClick={() => applySuggestion(s)}
                  onMouseEnter={() => setSelectedIndex(idx)}
                >
                  <div className="suggestion-main">
                    <span className="suggestion-command">@{s.command}:</span>
                    <span className="suggestion-label">{s.label}</span>
                  </div>
                  <div className="suggestion-extra">
                    <span className="suggestion-desc">{s.description}</span>
                    <span className="suggestion-badge">{s.example}</span>
                  </div>
                </li>
              ))}
            </ul>
          </div>
        )}
      </div>

      <button 
        className={`sort-button ${direction === 'desc' ? 'sort-desc' : 'sort-asc'}`}
        onClick={onDirectionToggle}
        title="Inverter ordenação"
      >
        <ArrowUpDown size={16} />
        <span>Ordem: {direction === 'asc' ? 'Crescente (A-Z)' : 'Decrescente (Z-A)'}</span>
      </button>
    </div>
  );
};
