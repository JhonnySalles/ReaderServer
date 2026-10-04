import React from 'react';
import { NavLink } from 'react-router-dom';
import { Home, BookOpen, Layers, Library, ChevronLeft, ChevronRight, RefreshCw } from 'lucide-react';
import { authStorage, authenticateAutomatically } from '../../services/api';
import './Sidebar.css';

interface SidebarProps {
  isOpen: boolean;
  onToggle: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ isOpen, onToggle }) => {
  const handleReauth = async () => {
    authStorage.clearAuth();
    await authenticateAutomatically();
    window.location.reload();
  };

  return (
    <aside className={`sidebar glass-panel ${isOpen ? 'sidebar-open' : 'sidebar-closed'}`}>
      <div className="sidebar-header">
        {isOpen ? (
          <>
            <div className="logo-container">
              <div className="logo-icon-wrapper">
                <Library className="logo-icon" size={24} />
              </div>
              <span className="logo-text gradient-text-primary">Reader Server</span>
            </div>
            <button 
              id="toggle-sidebar-btn"
              className="toggle-btn" 
              onClick={onToggle} 
              title="Recolher menu"
            >
              <ChevronLeft size={18} />
            </button>
          </>
        ) : (
          <button 
            id="toggle-sidebar-btn"
            className="toggle-btn-collapsed" 
            onClick={onToggle} 
            title="Expandir menu"
          >
            <ChevronRight size={20} />
          </button>
        )}
      </div>

      <nav className="sidebar-nav">
        <NavLink 
          to="/" 
          className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
          title="Início"
        >
          <Home size={20} />
          {isOpen && <span className="nav-label">Início</span>}
        </NavLink>

        <div className="nav-divider" />

        <NavLink 
          to="/books" 
          className={({ isActive }) => `nav-item ${isActive ? 'active book-active' : ''}`}
          title="Livros & OPF"
        >
          <BookOpen size={20} className="nav-icon-book" />
          {isOpen && <span className="nav-label">Livros & Epubs</span>}
        </NavLink>

        <NavLink 
          to="/mangas" 
          className={({ isActive }) => `nav-item ${isActive ? 'active manga-active' : ''}`}
          title="Mangás & ComicInfo"
        >
          <Layers size={20} className="nav-icon-manga" />
          {isOpen && <span className="nav-label">Mangás & Comics</span>}
        </NavLink>
      </nav>

      <div className="sidebar-footer">
        {isOpen ? (
          <>
            <button 
              onClick={handleReauth} 
              className="btn-sync-action" 
              title="Sincronizar / Renovar Acesso"
              id="btn-reauth"
            >
              <RefreshCw size={16} />
              <span>Sincronizar Acesso</span>
            </button>
            <div className="server-status">
              <span className="status-dot"></span>
              <span className="status-text">API Online :8083</span>
            </div>
          </>
        ) : (
          <button 
            onClick={handleReauth} 
            className="btn-sync-collapsed" 
            title="Sincronizar Acesso"
            id="btn-reauth-collapsed"
          >
            <RefreshCw size={18} />
          </button>
        )}
      </div>
    </aside>
  );
};

