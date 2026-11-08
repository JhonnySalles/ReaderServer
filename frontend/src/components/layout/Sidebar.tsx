import React, { useState, useEffect } from 'react';
import { NavLink } from 'react-router-dom';
import { Home, BookOpen, Layers, Library, ChevronLeft, ChevronRight, LogIn, LogOut, User } from 'lucide-react';
import { authStorage, logout, subscribeAuth } from '../../services/api';
import './Sidebar.css';

interface SidebarProps {
  isOpen: boolean;
  onToggle: () => void;
  onOpenLogin?: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ isOpen, onToggle, onOpenLogin }) => {
  const [currentUser, setCurrentUser] = useState<string>(authStorage.getUsername());
  const [isAuth, setIsAuth] = useState<boolean>(authStorage.isAuthenticated());

  useEffect(() => {
    const unsubscribe = subscribeAuth((event, user) => {
      if (event === 'login') {
        setIsAuth(true);
        setCurrentUser(user || authStorage.getUsername());
      } else if (event === 'logout' || event === 'unauthorized') {
        setIsAuth(false);
      }
    });
    return unsubscribe;
  }, []);

  const handleLogout = () => {
    logout();
    if (onOpenLogin) {
      onOpenLogin();
    } else {
      window.location.reload();
    }
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
            <div className="user-badge-container">
              <div className="user-badge-icon">
                <User size={14} />
              </div>
              <div className="user-badge-info">
                <span className="user-badge-label">{isAuth ? 'Conectado como' : 'Sessão Anônima'}</span>
                <span className="user-badge-name" title={currentUser}>{currentUser}</span>
              </div>
            </div>

            {isAuth ? (
              <button 
                onClick={handleLogout} 
                className="btn-sync-action btn-logout-action" 
                title="Encerrar sessão em memória"
                id="btn-logout"
              >
                <LogOut size={16} />
                <span>Sair / Trocar Usuário</span>
              </button>
            ) : (
              <button 
                onClick={onOpenLogin} 
                className="btn-sync-action btn-login-action" 
                title="Entrar com usuário e senha"
                id="btn-login-trigger"
              >
                <LogIn size={16} />
                <span>Fazer Login</span>
              </button>
            )}

            <div className="server-status">
              <span className={`status-dot ${isAuth ? 'status-online' : 'status-offline'}`}></span>
              <span className="status-text">{isAuth ? 'Sessão Ativa' : 'Não Autenticado'}</span>
            </div>
          </>
        ) : (
          isAuth ? (
            <button 
              onClick={handleLogout} 
              className="btn-sync-collapsed btn-logout-action" 
              title={`Sair (${currentUser})`}
              id="btn-logout-collapsed"
            >
              <LogOut size={18} />
            </button>
          ) : (
            <button 
              onClick={onOpenLogin} 
              className="btn-sync-collapsed btn-login-action" 
              title="Fazer Login"
              id="btn-login-collapsed"
            >
              <LogIn size={18} />
            </button>
          )
        )}
      </div>
    </aside>
  );
};
