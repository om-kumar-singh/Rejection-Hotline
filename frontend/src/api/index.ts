import { api } from './client';
import type {
  AnalyticsRates,
  AuthResponse,
  CompanyCount,
  DashboardSummary,
  JobApplication,
  MonthlyCount,
  Notification,
  PageResponse,
  User,
} from '../types';

export const authApi = {
  register: (data: { email: string; password: string; fullName: string }) =>
    api.post<AuthResponse>('/auth/register', data),
  login: (data: { email: string; password: string }) =>
    api.post<AuthResponse>('/auth/login', data),
  me: () => api.get<User>('/auth/me'),
};

export const applicationsApi = {
  list: (params: Record<string, string | number | undefined>) =>
    api.get<PageResponse<JobApplication>>('/applications', { params }),
  get: (id: number) => api.get<JobApplication>(`/applications/${id}`),
  create: (data: Partial<JobApplication>) => api.post<JobApplication>('/applications', data),
  update: (id: number, data: Partial<JobApplication>) =>
    api.put<JobApplication>(`/applications/${id}`, data),
  delete: (id: number) => api.delete(`/applications/${id}`),
  markFollowUpSent: (id: number) =>
    api.post<JobApplication>(`/applications/${id}/mark-follow-up-sent`),
  resetFollowUp: (id: number) =>
    api.post<JobApplication>(`/applications/${id}/reset-follow-up`),
};

export const dashboardApi = {
  summary: () => api.get<DashboardSummary>('/dashboard/summary'),
};

export const notificationsApi = {
  list: (page = 0, size = 20) =>
    api.get<PageResponse<Notification>>('/notifications', { params: { page, size } }),
  unreadCount: () => api.get<{ count: number }>('/notifications/unread-count'),
  markRead: (id: number) => api.patch<Notification>(`/notifications/${id}/read`),
  markAllRead: () => api.patch('/notifications/read-all'),
};

export const analyticsApi = {
  byMonth: () => api.get<{ data: MonthlyCount[] }>('/analytics/applications-by-month'),
  byCompany: (limit = 10) =>
    api.get<{ data: CompanyCount[] }>('/analytics/by-company', { params: { limit } }),
  rates: () => api.get<AnalyticsRates>('/analytics/rates'),
};

export const importApi = {
  previewExcel: (file: File) => {
    const form = new FormData();
    form.append('file', file);
    return api.post('/import/excel/preview', form, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
  importExcel: (file: File) => {
    const form = new FormData();
    form.append('file', file);
    return api.post('/import/excel', form, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
  googleAuthUrl: () => api.get<{ authUrl: string }>('/import/google/auth-url'),
  importGoogleSheets: (spreadsheetId: string, range?: string) =>
    api.post('/import/google-sheets', { spreadsheetId, range }),
};
