import axios, { AxiosError } from 'axios';
import type { InternalAxiosRequestConfig } from 'axios';

export interface AuthTokens {
  username: string;
  authenticated: boolean;
  accessToken: string;
  refreshToken: string;
}

export type AuthEventType = 'login' | 'logout' | 'unauthorized';
export type AuthEventListener = (event: AuthEventType, username?: string) => void;

const TOKEN_KEY = 'reader_access_token';
const REFRESH_TOKEN_KEY = 'reader_refresh_token';
const USERNAME_KEY = 'reader_username';

// Listeners para sincronizar estado de autenticação reativamente
const authListeners = new Set<AuthEventListener>();

export const subscribeAuth = (listener: AuthEventListener): (() => void) => {
  authListeners.add(listener);
  return () => {
    authListeners.delete(listener);
  };
};

export const notifyAuth = (event: AuthEventType, username?: string): void => {
  authListeners.forEach(listener => {
    try {
      listener(event, username);
    } catch (e) {
      console.error('Erro no listener de autenticação:', e);
    }
  });
};

// Helper para obter variáveis dinamicamente no Docker, Alwaysdata ou via .env em desenvolvimento
declare global {
  interface Window {
    __RUNTIME_CONFIG__?: {
      VITE_BASE_PATH?: string;
      VITE_API_BASE_URL?: string;
      VITE_API_USERNAME?: string;
      VITE_API_KEY?: string;
    };
  }
}

const getEnv = (key: 'VITE_API_BASE_URL' | 'VITE_API_USERNAME' | 'VITE_API_KEY', fallback: string): string => {
  if (typeof window !== 'undefined' && window.__RUNTIME_CONFIG__ && window.__RUNTIME_CONFIG__[key]) {
    return window.__RUNTIME_CONFIG__[key]!;
  }
  return (import.meta.env[key] as string) || fallback;
};

export const authStorage = {
  getToken: () => sessionStorage.getItem(TOKEN_KEY),
  getRefreshToken: () => sessionStorage.getItem(REFRESH_TOKEN_KEY),
  getUsername: () => sessionStorage.getItem(USERNAME_KEY) || getEnv('VITE_API_USERNAME', 'admin'),
  saveAuth: (tokens: AuthTokens) => {
    sessionStorage.setItem(TOKEN_KEY, tokens.accessToken);
    sessionStorage.setItem(REFRESH_TOKEN_KEY, tokens.refreshToken);
    sessionStorage.setItem(USERNAME_KEY, tokens.username);
  },
  clearAuth: () => {
    sessionStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(REFRESH_TOKEN_KEY);
    sessionStorage.removeItem(USERNAME_KEY);
  },
  isAuthenticated: () => {
    return !!sessionStorage.getItem(TOKEN_KEY);
  }
};

export const getBaseUrl = (): string => {
  const configuredUrl = getEnv('VITE_API_BASE_URL', '');
  if (configuredUrl) {
    return configuredUrl;
  }
  if (typeof window !== 'undefined' && window.location.hostname !== 'localhost' && window.location.hostname !== '127.0.0.1') {
    return `${window.location.origin}/readerserver/api`;
  }
  return 'http://localhost:8083';
};

export const getApiKey = () => getEnv('VITE_API_KEY', '');

export async function login(username: string, password: string): Promise<AuthTokens> {
  const apiKey = getApiKey();
  const baseUrl = getBaseUrl();

  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  };
  if (apiKey) {
    headers['X-API-KEY'] = apiKey;
  }

  const response = await axios.post<AuthTokens>(`${baseUrl}/auth/signin`, {
    username: username.trim(),
    password
  }, {
    headers
  });

  if (response.data && response.data.accessToken) {
    authStorage.saveAuth(response.data);
    notifyAuth('login', response.data.username);
    return response.data;
  }

  throw new Error('Falha no login: token de acesso não retornado pela API.');
}

export function logout(): void {
  authStorage.clearAuth();
  notifyAuth('logout');
}

/**
 * Mantido para retrocompatibilidade; dispara o fluxo de login interativo.
 */
export async function authenticateAutomatically(): Promise<string | null> {
  notifyAuth('unauthorized');
  return null;
}

const api = axios.create({
  baseURL: getBaseUrl(),
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
});

// Request Interceptor: adiciona X-API-KEY e Authorization Bearer em memória
api.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const apiKey = getApiKey();
  if (apiKey && config.headers) {
    config.headers['X-API-KEY'] = apiKey;
  }

  const token = authStorage.getToken();
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
}, (error) => {
  return Promise.reject(error);
});

// Response Interceptor: se receber 401 ou 403, limpa a sessão e notifica a interface para abrir o modal de login
api.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    if (error.response && (error.response.status === 401 || error.response.status === 403)) {
      authStorage.clearAuth();
      notifyAuth('unauthorized');
    }
    return Promise.reject(error);
  }
);

export default api;
