import React from 'react';
import { NavLink } from 'react-router-dom';
import { Home, BookOpen, Layers, Library, ChevronLeft, ChevronRight, RefreshCw, User } from 'lucide-react';
import { authStorage, authenticateAutomatically } from '../../services/api';
import './Sidebar.css';

interface SidebarProps {
  isOpen: boolean;
  onToggle: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ isOpen, onToggle }) => {
  const username = authStorage.getUsername();

  const handleReauth = async () => {
    authStorage.clearAuth();
    await authenticateAutomatically();
    window.location.reload();
  };

  return (
    <aside className={`sidebar glass-panel ${isOpen ? 'sidebar-open' : 'sidebar-closed'}`}>
      <div className="sidebar-header">
        <div className="logo-container">
          <div className="logo-icon-wrapper">
            <Library className="logo-icon" size={24} />
          </div>
          {isOpen && <span className="logo-text gradient-text-primary">Reader Server</span>}
        </div>
        <button 
          id="toggle-sidebar-btn"
          className="toggle-btn" 
          onClick={onToggle} 
          title={isOpen ? "Recolher menu" : "Expandir menu"}
        >
          {isOpen ? <ChevronLeft size={18} /> : <ChevronRight size={18} />}
        </button>
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
        {isOpen && (
          <div className="user-profile-badge">
            <div className="user-avatar">
              <User size={16} />
            </div>
            <span className="user-name">{username}</span>
            <button 
              onClick={handleReauth} 
              className="btn-logout" 
              title="Sincronizar / Renovar Acesso"
              id="btn-reauth"
            >
              <RefreshCw size={14} />
            </button>
          </div>
        )}
        
        {isOpen && (
          <div className="server-status">
            <span className="status-dot"></span>
            <span className="status-text">API Online :8083</span>
          </div>
        )}
        
        {!isOpen && (
          <button 
            onClick={handleReauth} 
            className="btn-logout-collapsed" 
            title="Sincronizar Acesso"
          >
            <RefreshCw size={16} />
          </button>
        )}
      </div>
    </aside>
  );
};
