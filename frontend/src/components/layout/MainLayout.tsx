import React, { useState } from 'react';
import { Outlet } from 'react-router-dom';
import { Sidebar } from './Sidebar';
import { LoginModal } from '../auth/LoginModal';
import './MainLayout.css';

export const MainLayout: React.FC = () => {
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [loginModalOpen, setLoginModalOpen] = useState(false);

  return (
    <div className="layout-container">
      <Sidebar 
        isOpen={sidebarOpen} 
        onToggle={() => setSidebarOpen(!sidebarOpen)} 
        onOpenLogin={() => setLoginModalOpen(true)}
      />
      <main className={`main-content ${sidebarOpen ? 'main-expanded' : 'main-collapsed'}`}>
        <div className="content-inner animate-fade-in">
          <Outlet />
        </div>
      </main>
      <LoginModal 
        isOpen={loginModalOpen} 
        onClose={() => setLoginModalOpen(false)}
        onSuccess={() => {
          setLoginModalOpen(false);
          window.location.reload();
        }}
      />
    </div>
  );
};
