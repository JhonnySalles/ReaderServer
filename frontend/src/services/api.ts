import axios, { AxiosError } from 'axios';
import type { InternalAxiosRequestConfig } from 'axios';

export interface AuthTokens {
  username: string;
  authenticated: boolean;
  accessToken: string;
  refreshToken: string;
}

const TOKEN_KEY = 'reader_access_token';
const REFRESH_TOKEN_KEY = 'reader_refresh_token';
const USERNAME_KEY = 'reader_username';

// Helper para obter variáveis dinamicamente no Docker ou via .env em desenvolvimento
declare global {
  interface Window {
    __RUNTIME_CONFIG__?: {
      VITE_API_BASE_URL?: string;
      VITE_API_USERNAME?: string;
      VITE_API_PASSWORD?: string;
      VITE_API_KEY?: string;
    };
  }
}

const getEnv = (key: 'VITE_API_BASE_URL' | 'VITE_API_USERNAME' | 'VITE_API_PASSWORD' | 'VITE_API_KEY', fallback: string): string => {
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

export const getBaseUrl = () => getEnv('VITE_API_BASE_URL', 'http://localhost:8083');
export const getApiKey = () => getEnv('VITE_API_KEY', '');

const api = axios.create({
  baseURL: getBaseUrl(),
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
});

// Promessa singleton para evitar chamadas de login concorrentes
let loginPromise: Promise<string | null> | null = null;

export async function authenticateAutomatically(): Promise<string | null> {
  if (loginPromise) {
    return loginPromise;
  }

  loginPromise = (async () => {
    try {
      const username = getEnv('VITE_API_USERNAME', 'admin');
      const password = getEnv('VITE_API_PASSWORD', 'admin');
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
        username,
        password
      }, {
        headers
      });

      if (response.data && response.data.accessToken) {
        authStorage.saveAuth(response.data);
        return response.data.accessToken;
      }
      return null;
    } catch (err) {
      console.error('Falha na autenticação automática do ReaderServer:', err);
      return null;
    } finally {
      loginPromise = null;
    }
  })();

  return loginPromise;
}

// Request Interceptor: Garante que o token está presente antes da requisição
api.interceptors.request.use(async (config: InternalAxiosRequestConfig) => {
  const apiKey = getApiKey();
  if (apiKey && config.headers) {
    config.headers['X-API-KEY'] = apiKey;
  }

  let token = authStorage.getToken();

  // Se não temos token em memória e a rota for privada (/api), autentica antes de disparar
  if (!token && config.url && !config.url.startsWith('/auth') && !config.url.startsWith('/health')) {
    token = await authenticateAutomatically();
  }

  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
}, (error) => {
  return Promise.reject(error);
});

// Response Interceptor: Se receber 401 ou 403, tenta renovar o token automaticamente e reprocessar
api.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as (InternalAxiosRequestConfig & { _retry?: boolean }) | undefined;

    if (error.response && (error.response.status === 401 || error.response.status === 403) && originalRequest && !originalRequest._retry) {
      originalRequest._retry = true;
      authStorage.clearAuth();

      const newToken = await authenticateAutomatically();
      if (newToken && originalRequest.headers) {
        originalRequest.headers.Authorization = `Bearer ${newToken}`;
        return api(originalRequest);
      }
    }

    return Promise.reject(error);
  }
);

export default api;
