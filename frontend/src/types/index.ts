export interface User {
  id: number;
  email: string;
  fullName: string;
  createdAt: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  user: User;
}

export interface JobApplication {
  id: number;
  hrName?: string;
  hrEmail?: string;
  companyName: string;
  jobRole: string;
  appliedDate: string;
  emailStatus: 'SENT' | 'DRAFT';
  replyReceived: boolean;
  followUpSentCount: number;
  followUpReminderEnabled: boolean;
  applicationStatus: 'APPLIED' | 'WAITING' | 'INTERVIEW' | 'REJECTED' | 'OFFER';
  notes?: string;
  importSource: string;
  needFollowUp: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface DashboardSummary {
  totalApplications: number;
  appliedToday: number;
  needFollowUp: number;
  interviews: number;
  rejected: number;
  offers: number;
  responseRate: number;
  followUpSuccessRate: number;
}

export interface Notification {
  id: number;
  jobApplicationId?: number;
  type: string;
  title: string;
  message: string;
  read: boolean;
  createdAt: string;
}

export interface MonthlyCount {
  month: string;
  count: number;
}

export interface CompanyCount {
  companyName: string;
  count: number;
}

export interface AnalyticsRates {
  responseRate: number;
  followUpRate: number;
  interviewRate: number;
  offerRate: number;
}
