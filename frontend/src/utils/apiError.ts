import axios, { AxiosError } from 'axios';

interface ApiErrorBody {
  message?: string;
  error?: string;
  errors?: Record<string, string>;
}

export function getApiErrorMessage(error: unknown, fallback: string): string {
  if (axios.isAxiosError(error)) {
    const axiosError = error as AxiosError<ApiErrorBody>;
    if (!axiosError.response) {
      return 'Cannot reach the server. Ensure the backend is running on http://localhost:8080.';
    }
    const data = axiosError.response.data;
    if (data?.message) return data.message;
    if (data?.error) return data.error;
    if (data?.errors) {
      return Object.values(data.errors).join(', ');
    }
    return `Request failed (${axiosError.response.status})`;
  }
  if (error instanceof Error) return error.message;
  return fallback;
}
