import axios from 'axios';
import { useAuthStore } from '../store/authStore';

const API_BASE = import.meta.env.VITE_API_BASE_URL || '/api/v1';

export const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use((config) => {
  const url = config.url ?? '';
  const isPublicAuth =
    url.includes('/auth/register') ||
    url.includes('/auth/login') ||
    url.includes('/auth/refresh');

  if (!isPublicAuth) {
    const token = useAuthStore.getState().accessToken;
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
  } else {
    delete config.headers.Authorization;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const axiosError = error as { response?: { status?: number; config?: { url?: string } } };
    if (axiosError.response?.status === 401) {
      const url = axiosError.response.config?.url ?? '';
      if (!url.includes('/auth/login') && !url.includes('/auth/register')) {
        useAuthStore.getState().logout();
      }
    }
    return Promise.reject(error);
  }
);
