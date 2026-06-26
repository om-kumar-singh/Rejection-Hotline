import { useQuery } from '@tanstack/react-query';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, LineChart, Line } from 'recharts';
import { analyticsApi } from '../api';

export function AnalyticsPage() {
  const { data: monthly } = useQuery({
    queryKey: ['analytics-monthly'],
    queryFn: () => analyticsApi.byMonth().then((r) => r.data.data),
  });

  const { data: companies } = useQuery({
    queryKey: ['analytics-companies'],
    queryFn: () => analyticsApi.byCompany().then((r) => r.data.data),
  });

  const { data: rates } = useQuery({
    queryKey: ['analytics-rates'],
    queryFn: () => analyticsApi.rates().then((r) => r.data),
  });

  return (
    <div>
      <div className="page-header">
        <h2>Analytics</h2>
      </div>
      <div className="charts-grid">
        <div className="chart-card">
          <h3>Applications per Month</h3>
          <ResponsiveContainer width="100%" height={250}>
            <LineChart data={monthly}>
              <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.08)" />
              <XAxis dataKey="month" stroke="#6b7280" tick={{ fill: '#9ca3af', fontSize: 12 }} />
              <YAxis stroke="#6b7280" tick={{ fill: '#9ca3af', fontSize: 12 }} />
              <Tooltip contentStyle={{ background: '#111827', border: '1px solid rgba(255,255,255,0.08)' }} />
              <Line type="monotone" dataKey="count" stroke="#FE7F2D" strokeWidth={2} dot={{ fill: '#FE7F2D' }} />
            </LineChart>
          </ResponsiveContainer>
        </div>
        <div className="chart-card">
          <h3>Top Companies</h3>
          <ResponsiveContainer width="100%" height={250}>
            <BarChart data={companies}>
              <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.08)" />
              <XAxis dataKey="companyName" stroke="#6b7280" tick={{ fill: '#9ca3af', fontSize: 12 }} />
              <YAxis stroke="#6b7280" tick={{ fill: '#9ca3af', fontSize: 12 }} />
              <Tooltip contentStyle={{ background: '#111827', border: '1px solid rgba(255,255,255,0.08)' }} />
              <Bar dataKey="count" fill="#FE7F2D" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>
      {rates && (
        <div className="kpi-grid">
          <div className="kpi-card"><span className="kpi-label">Response Rate</span><span className="kpi-value">{rates.responseRate}%</span></div>
          <div className="kpi-card"><span className="kpi-label">Follow-up Rate</span><span className="kpi-value">{rates.followUpRate}%</span></div>
          <div className="kpi-card"><span className="kpi-label">Interview Rate</span><span className="kpi-value">{rates.interviewRate}%</span></div>
          <div className="kpi-card"><span className="kpi-label">Offer Rate</span><span className="kpi-value">{rates.offerRate}%</span></div>
        </div>
      )}
    </div>
  );
}
