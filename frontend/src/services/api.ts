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

export const authStorage = {
  getToken: () => sessionStorage.getItem(TOKEN_KEY),
  getRefreshToken: () => sessionStorage.getItem(REFRESH_TOKEN_KEY),
  getUsername: () => sessionStorage.getItem(USERNAME_KEY) || (import.meta.env.VITE_API_USERNAME as string) || 'admin',
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

const BASE_URL = (import.meta.env.VITE_API_BASE_URL as string) || 'http://localhost:8083';

const api = axios.create({
  baseURL: BASE_URL,
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
      const username = (import.meta.env.VITE_API_USERNAME as string) || 'admin';
      const password = (import.meta.env.VITE_API_PASSWORD as string) || 'admin';

      const response = await axios.post<AuthTokens>(`${BASE_URL}/auth/signin`, {
        username,
        password
      }, {
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        }
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
