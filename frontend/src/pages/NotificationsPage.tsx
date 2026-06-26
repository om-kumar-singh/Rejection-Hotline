import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { notificationsApi } from '../api';

export function NotificationsPage() {
  const queryClient = useQueryClient();

  const { data, isLoading } = useQuery({
    queryKey: ['notifications'],
    queryFn: () => notificationsApi.list().then((r) => r.data),
  });

  const markAll = useMutation({
    mutationFn: () => notificationsApi.markAllRead(),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['notifications'] }),
  });

  const markRead = useMutation({
    mutationFn: (id: number) => notificationsApi.markRead(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['notifications'] }),
  });

  return (
    <div>
      <div className="page-header">
        <h2>Notifications</h2>
        <button className="btn-secondary" onClick={() => markAll.mutate()}>Mark All Read</button>
      </div>
      {isLoading ? <div className="loading">Loading...</div> : (
        <div className="notification-list">
          {data?.content.map((n) => (
            <div key={n.id} className={`notification-item ${n.read ? 'read' : 'unread'}`}>
              <div>
                <strong>{n.title}</strong>
                <p>{n.message}</p>
                <small>{new Date(n.createdAt).toLocaleString()}</small>
              </div>
              <div className="notification-actions">
                {n.jobApplicationId && (
                  <Link to={`/applications/${n.jobApplicationId}`}>View Application</Link>
                )}
                {!n.read && (
                  <button onClick={() => markRead.mutate(n.id)}>Mark Read</button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
