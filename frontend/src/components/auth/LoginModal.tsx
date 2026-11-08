import React, { useState, useEffect } from 'react';
import { createPortal } from 'react-dom';
import { Lock, User, Eye, EyeOff, LogIn, Loader2, AlertCircle, ShieldCheck, X } from 'lucide-react';
import { login, authStorage, subscribeAuth } from '../../services/api';
import './LoginModal.css';

interface LoginModalProps {
  isOpen?: boolean;
  onClose?: () => void;
  onSuccess?: () => void;
}

export const LoginModal: React.FC<LoginModalProps> = ({
  isOpen: controlledIsOpen,
  onClose,
  onSuccess
}) => {
  const [internalIsOpen, setInternalIsOpen] = useState(false);
  const [username, setUsername] = useState(authStorage.getUsername());
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  // Escuta eventos de 401 / desautenticação global para abrir o modal automaticamente
  useEffect(() => {
    const unsubscribe = subscribeAuth((event) => {
      if (event === 'unauthorized') {
        setInternalIsOpen(true);
        setErrorMessage('Sessão expirada ou não autorizada. Faça login para continuar.');
      }
    });
    return unsubscribe;
  }, []);

  const isOpen = controlledIsOpen !== undefined ? (controlledIsOpen || internalIsOpen) : internalIsOpen;

  const handleClose = () => {
    setInternalIsOpen(false);
    if (onClose) {
      onClose();
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!username.trim() || !password) {
      setErrorMessage('Por favor, informe o usuário e a senha.');
      return;
    }

    setLoading(true);
    setErrorMessage(null);

    try {
      await login(username, password);
      setPassword('');
      handleClose();

      if (onSuccess) {
        onSuccess();
      } else {
        // Recarrega para reinicializar todas as consultas ativas com o novo token em memória
        window.location.reload();
      }
    } catch (err: any) {
      console.error('Falha no login:', err);
      if (err.response) {
        if (err.response.status === 401) {
          setErrorMessage('Usuário ou senha inválidos.');
        } else if (err.response.status === 403) {
          setErrorMessage('Acesso não autorizado. Verifique se a API Key está configurada corretamente.');
        } else {
          setErrorMessage(`Falha na autenticação (${err.response.status}). Tente novamente.`);
        }
      } else if (err.request) {
        setErrorMessage('Não foi possível conectar ao servidor da API. Verifique se o serviço está ativo.');
      } else {
        setErrorMessage(err.message || 'Erro ao realizar login.');
      }
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen) return null;

  return createPortal(
    <div className="login-modal-backdrop" role="dialog" aria-modal="true">
      <div className="login-modal-card glass-panel" onClick={(e) => e.stopPropagation()}>
        {onClose && (
          <button 
            type="button" 
            className="login-modal-close-btn" 
            onClick={handleClose} 
            title="Fechar"
            disabled={loading}
          >
            <X size={18} />
          </button>
        )}

        <div className="login-modal-header">
          <div className="login-icon-badge">
            <ShieldCheck size={28} className="shield-icon" />
          </div>
          <h2 className="login-modal-title">Acesso ao ReaderServer</h2>
          <p className="login-modal-subtitle">
            Informe suas credenciais para gerenciar e visualizar a biblioteca de livros e mangás.
          </p>
        </div>

        <form onSubmit={handleSubmit} className="login-form">
          {errorMessage && (
            <div className="login-error-banner animate-fade-in" role="alert">
              <AlertCircle size={18} className="error-icon" />
              <span>{errorMessage}</span>
            </div>
          )}

          <div className="login-input-group">
            <label htmlFor="login-username" className="login-label">Usuário</label>
            <div className="login-input-wrapper">
              <User size={18} className="input-icon" />
              <input
                id="login-username"
                type="text"
                className="login-input"
                placeholder="Nome de usuário"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                disabled={loading}
                autoFocus={!username}
                required
              />
            </div>
          </div>

          <div className="login-input-group">
            <label htmlFor="login-password" className="login-label">Senha</label>
            <div className="login-input-wrapper">
              <Lock size={18} className="input-icon" />
              <input
                id="login-password"
                type={showPassword ? 'text' : 'password'}
                className="login-input"
                placeholder="Digite sua senha"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                disabled={loading}
                autoFocus={!!username}
                required
              />
              <button
                type="button"
                className="password-toggle-btn"
                onClick={() => setShowPassword(!showPassword)}
                tabIndex={-1}
                title={showPassword ? 'Ocultar senha' : 'Exibir senha'}
              >
                {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>
          </div>

          <button
            type="submit"
            className="login-submit-btn"
            disabled={loading || !username.trim() || !password}
            id="btn-login-submit"
          >
            {loading ? (
              <>
                <Loader2 size={18} className="animate-spin" />
                <span>Autenticando...</span>
              </>
            ) : (
              <>
                <LogIn size={18} />
                <span>Entrar no Sistema</span>
              </>
            )}
          </button>
        </form>

        <div className="login-modal-footer">
          <span className="security-tag">
            🔒 Credenciais e tokens seguros mantidos estritamente em memória temporária de sessão.
          </span>
        </div>
      </div>
    </div>,
    document.body
  );
};
