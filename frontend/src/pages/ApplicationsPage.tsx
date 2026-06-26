import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { applicationsApi } from '../api';

const FILTERS = [
  '', 'APPLIED_TODAY', 'APPLIED_THIS_WEEK', 'NEED_FOLLOW_UP', 'WAITING',
  'INTERVIEW', 'REJECTED', 'OFFER',
];

export function ApplicationsPage() {
  const [search, setSearch] = useState('');
  const [filter, setFilter] = useState('');
  const [page, setPage] = useState(0);

  const { data, isLoading } = useQuery({
    queryKey: ['applications', search, filter, page],
    queryFn: () =>
      applicationsApi.list({ search: search || undefined, filter: filter || undefined, page, size: 10 })
        .then((r) => r.data),
  });

  return (
    <div>
      <div className="page-header">
        <h2>Applications</h2>
        <Link to="/applications/new" className="btn btn-primary">Add Application</Link>
      </div>
      <div className="filters">
        <input placeholder="Search company, HR, role..." value={search} onChange={(e) => { setSearch(e.target.value); setPage(0); }} />
        <select value={filter} onChange={(e) => { setFilter(e.target.value); setPage(0); }}>
          <option value="">All</option>
          {FILTERS.filter(Boolean).map((f) => (
            <option key={f} value={f}>{f.replace(/_/g, ' ')}</option>
          ))}
        </select>
      </div>
      {isLoading ? <div className="loading">Loading...</div> : (
        <table className="data-table">
          <thead>
            <tr>
              <th>Company</th><th>Role</th><th>Applied</th><th>Status</th><th>Follow-up</th><th></th>
            </tr>
          </thead>
          <tbody>
            {data?.content.map((app) => (
              <tr key={app.id} className={app.needFollowUp ? 'row-highlight' : ''}>
                <td>{app.companyName}</td>
                <td>{app.jobRole}</td>
                <td>{app.appliedDate}</td>
                <td>{app.applicationStatus}</td>
                <td>{app.needFollowUp ? 'Due' : app.followUpSentCount > 0 ? 'Sent' : '-'}</td>
                <td><Link to={`/applications/${app.id}`}>View</Link></td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
      <div className="pagination">
        <button disabled={page === 0} onClick={() => setPage((p) => p - 1)}>Prev</button>
        <span>Page {page + 1} of {data?.totalPages ?? 1}</span>
        <button disabled={data?.last} onClick={() => setPage((p) => p + 1)}>Next</button>
      </div>
    </div>
  );
}
