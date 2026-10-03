import React from 'react';
import { Search, ArrowUpDown, X } from 'lucide-react';
import './FilterBar.css';

interface FilterBarProps {
  searchQuery: string;
  onSearchChange: (value: string) => void;
  direction: 'asc' | 'desc';
  onDirectionToggle: () => void;
  placeholder?: string;
}

export const FilterBar: React.FC<FilterBarProps> = ({
  searchQuery,
  onSearchChange,
  direction,
  onDirectionToggle,
  placeholder = "Filtrar por nome ou arquivo..."
}) => {
  return (
    <div className="filter-bar glass-panel">
      <div className="search-input-wrapper">
        <Search size={18} className="search-icon" />
        <input
          type="text"
          className="search-input"
          placeholder={placeholder}
          value={searchQuery}
          onChange={(e) => onSearchChange(e.target.value)}
        />
        {searchQuery && (
          <button 
            className="clear-search-btn" 
            onClick={() => onSearchChange('')}
            title="Limpar busca"
          >
            <X size={16} />
          </button>
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
