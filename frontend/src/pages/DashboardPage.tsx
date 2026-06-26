import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { dashboardApi } from '../api';

export function DashboardPage() {
  const { data, isLoading } = useQuery({
    queryKey: ['dashboard'],
    queryFn: () => dashboardApi.summary().then((r) => r.data),
  });

  if (isLoading) return <div className="loading">Loading dashboard...</div>;

  const cards = [
    { label: 'Total Applications', value: data?.totalApplications ?? 0 },
    { label: 'Applied Today', value: data?.appliedToday ?? 0 },
    { label: 'Need Follow-up', value: data?.needFollowUp ?? 0, highlight: true },
    { label: 'Interviews', value: data?.interviews ?? 0 },
    { label: 'Rejected', value: data?.rejected ?? 0 },
    { label: 'Offers', value: data?.offers ?? 0 },
    { label: 'Response Rate', value: `${data?.responseRate ?? 0}%` },
    { label: 'Follow-up Success', value: `${data?.followUpSuccessRate ?? 0}%` },
  ];

  return (
    <div>
      <div className="page-header">
        <h2>Dashboard</h2>
        <Link to="/applications" className="btn btn-primary">View Applications</Link>
      </div>
      <div className="kpi-grid">
        {cards.map((card) => (
          <div key={card.label} className={`kpi-card ${card.highlight ? 'highlight' : ''}`}>
            <span className="kpi-label">{card.label}</span>
            <span className="kpi-value">{card.value}</span>
          </div>
        ))}
      </div>
    </div>
  );
}
