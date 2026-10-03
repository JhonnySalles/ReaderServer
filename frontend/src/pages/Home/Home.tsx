import React from 'react';
import { Link } from 'react-router-dom';
import { BookOpen, Layers, ArrowRight, Sparkles, Database, FileText } from 'lucide-react';
import './Home.css';

export const Home: React.FC = () => {
  return (
    <div className="home-container">
      <header className="home-header">
        <div className="badge-pill">
          <Sparkles size={14} className="sparkle-icon" />
          <span>Servidor de Biblioteca & Metadados</span>
        </div>
        <h1 className="home-title">
          Gerencie e explore sua biblioteca de <span className="gradient-text-primary">Livros e Mangás</span>
        </h1>
        <p className="home-subtitle">
          Acesse arquivos físicos de leitura integrados com metadados estruturados de ComicInfo e OPF.
        </p>
      </header>

      <div className="nav-cards-grid">
        {/* Card Livros */}
        <Link to="/books" className="portal-card book-portal-card glass-panel" id="btn-portal-books">
          <div className="portal-glow book-glow" />
          <div className="portal-icon-wrapper book-icon-bg">
            <BookOpen size={36} />
          </div>
          <div className="portal-content">
            <div className="portal-tag book-tag">Biblioteca Digital</div>
            <h2 className="portal-title">Livros & Epubs</h2>
            <p className="portal-desc">
              Explore arquivos de ePubs, PDFs e seus respectivos metadados do padrão Open Packaging Format (OPF).
            </p>
            <div className="portal-action book-action">
              <span>Acessar Livros</span>
              <ArrowRight size={18} />
            </div>
          </div>
        </Link>

        {/* Card Mangás */}
        <Link to="/mangas" className="portal-card manga-portal-card glass-panel" id="btn-portal-mangas">
          <div className="portal-glow manga-glow" />
          <div className="portal-icon-wrapper manga-icon-bg">
            <Layers size={36} />
          </div>
          <div className="portal-content">
            <div className="portal-tag manga-tag">Quadrinhos & Webtoons</div>
            <h2 className="portal-title">Mangás & Comics</h2>
            <p className="portal-desc">
              Visualize volumes em CBZ/CBR com informações ricas extraídas diretamente do ComicInfo.xml.
            </p>
            <div className="portal-action manga-action">
              <span>Acessar Mangás</span>
              <ArrowRight size={18} />
            </div>
          </div>
        </Link>
      </div>

      <section className="home-features">
        <div className="feature-item glass-panel">
          <Database size={24} className="feature-icon" />
          <div>
            <h3>API REST Swagger</h3>
            <p>Integração com backend Kotlin Spring Boot e banco relacional.</p>
          </div>
        </div>
        <div className="feature-item glass-panel">
          <FileText size={24} className="feature-icon" />
          <div>
            <h3>Metadados & Raw Files</h3>
            <p>Suporte completo a consultas paginadas e download do conteúdo XML/OPF bruto.</p>
          </div>
        </div>
      </section>
    </div>
  );
};
