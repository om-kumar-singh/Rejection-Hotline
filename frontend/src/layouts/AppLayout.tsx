import { NavLink, Outlet } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { notificationsApi } from '../api';
import { useAuthStore } from '../store/authStore';

function EnvelopeIcon() {
  return (
    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
      <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" />
      <polyline points="22,6 12,13 2,6" />
    </svg>
  );
}

export function AppLayout() {
  const user = useAuthStore((s) => s.user);
  const logout = useAuthStore((s) => s.logout);

  const { data: unread } = useQuery({
    queryKey: ['unread-count'],
    queryFn: () => notificationsApi.unreadCount().then((r) => r.data.count),
    refetchInterval: 30000,
  });

  const navClass = ({ isActive }: { isActive: boolean }) =>
    isActive ? 'app-nav-link active' : 'app-nav-link';

  return (
    <div className="app-shell">
      <aside className="app-sidebar">
        <div className="app-brand">
          <span className="app-brand-icon">
            <EnvelopeIcon />
          </span>
          <h1>Rejection Hotline</h1>
        </div>
        <p>Follow-up tracker</p>
        <nav>
          <NavLink to="/dashboard" className={navClass}>Dashboard</NavLink>
          <NavLink to="/applications" className={navClass}>Applications</NavLink>
          <NavLink to="/notifications" className={navClass}>
            Notifications{unread ? ` (${unread})` : ''}
          </NavLink>
          <NavLink to="/analytics" className={navClass}>Analytics</NavLink>
          <NavLink to="/import" className={navClass}>Import</NavLink>
        </nav>
        <div className="app-sidebar-footer">
          <p>{user?.fullName}</p>
          <button className="btn btn-secondary btn-block" onClick={logout}>
            Logout
          </button>
        </div>
      </aside>
      <div className="app-main">
        <main className="app-content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
