import { useEffect, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { applicationsApi } from '../api';

import type { JobApplication } from '../types';

type ApplicationForm = {
  hrName: string;
  hrEmail: string;
  companyName: string;
  jobRole: string;
  appliedDate: string;
  emailStatus: JobApplication['emailStatus'];
  replyReceived: boolean;
  applicationStatus: JobApplication['applicationStatus'];
  notes: string;
};

const emptyForm: ApplicationForm = {
  hrName: '', hrEmail: '', companyName: '', jobRole: '', appliedDate: '',
  emailStatus: 'SENT', replyReceived: false, applicationStatus: 'APPLIED', notes: '',
};

export function ApplicationFormPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const isEdit = !!id && id !== 'new';

  const { data: existing } = useQuery({
    queryKey: ['application', id],
    queryFn: () => applicationsApi.get(Number(id)).then((r) => r.data),
    enabled: isEdit,
  });

  const [form, setForm] = useState<ApplicationForm>(emptyForm);

  useEffect(() => {
    if (existing) {
      setForm({
        hrName: existing.hrName || '',
        hrEmail: existing.hrEmail || '',
        companyName: existing.companyName,
        jobRole: existing.jobRole,
        appliedDate: existing.appliedDate,
        emailStatus: existing.emailStatus,
        replyReceived: existing.replyReceived,
        applicationStatus: existing.applicationStatus,
        notes: existing.notes || '',
      });
    }
  }, [existing]);

  const mutation = useMutation({
    mutationFn: (payload: ApplicationForm) =>
      isEdit ? applicationsApi.update(Number(id), payload as Partial<JobApplication>)
        : applicationsApi.create(payload as Partial<JobApplication>),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['applications'] });
      navigate('/applications');
    },
  });

  const handleChange = (field: keyof ApplicationForm, value: string | boolean) =>
    setForm((prev) => ({ ...prev, [field]: value }));

  return (
    <div>
      <div className="page-header">
        <h2>{isEdit ? 'Edit Application' : 'New Application'}</h2>
        <Link to="/applications">Back</Link>
      </div>
      <form className="form-card" onSubmit={(e) => { e.preventDefault(); mutation.mutate(form); }}>
        <label>Company Name<input required value={form.companyName} onChange={(e) => handleChange('companyName', e.target.value)} /></label>
        <label>Job Role<input required value={form.jobRole} onChange={(e) => handleChange('jobRole', e.target.value)} /></label>
        <label>HR Name<input value={form.hrName} onChange={(e) => handleChange('hrName', e.target.value)} /></label>
        <label>HR Email<input type="email" value={form.hrEmail} onChange={(e) => handleChange('hrEmail', e.target.value)} /></label>
        <label>Applied Date<input type="date" required value={form.appliedDate} onChange={(e) => handleChange('appliedDate', e.target.value)} /></label>
        <label>Email Status
          <select value={form.emailStatus} onChange={(e) => handleChange('emailStatus', e.target.value)}>
            <option value="SENT">SENT</option>
            <option value="DRAFT">DRAFT</option>
          </select>
        </label>
        <label>Application Status
          <select value={form.applicationStatus} onChange={(e) => handleChange('applicationStatus', e.target.value)}>
            {['APPLIED','WAITING','INTERVIEW','REJECTED','OFFER'].map((s) => (
              <option key={s} value={s}>{s}</option>
            ))}
          </select>
        </label>
        <label><input type="checkbox" checked={form.replyReceived} onChange={(e) => handleChange('replyReceived', e.target.checked)} /> Reply Received</label>
        <label>Notes<textarea value={form.notes} onChange={(e) => handleChange('notes', e.target.value)} /></label>
        <button type="submit" className="btn">{isEdit ? 'Update' : 'Create'}</button>
      </form>
    </div>
  );
}

export function ApplicationDetailPage() {
  const { id } = useParams();
  const queryClient = useQueryClient();

  const { data: app, isLoading } = useQuery({
    queryKey: ['application', id],
    queryFn: () => applicationsApi.get(Number(id)).then((r) => r.data),
  });

  const markFollowUp = useMutation({
    mutationFn: () => applicationsApi.markFollowUpSent(Number(id)),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['application', id] }),
  });

  const resetFollowUp = useMutation({
    mutationFn: () => applicationsApi.resetFollowUp(Number(id)),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['application', id] }),
  });

  if (isLoading || !app) return <div className="loading">Loading...</div>;

  return (
    <div>
      <div className="page-header">
        <h2>{app.companyName} — {app.jobRole}</h2>
        <Link to={`/applications/${id}/edit`} className="btn">Edit</Link>
      </div>
      <div className="detail-card">
        <p><strong>HR:</strong> {app.hrName} ({app.hrEmail})</p>
        <p><strong>Applied:</strong> {app.appliedDate}</p>
        <p><strong>Email Status:</strong> {app.emailStatus}</p>
        <p><strong>Reply Received:</strong> {app.replyReceived ? 'Yes' : 'No'}</p>
        <p><strong>Status:</strong> {app.applicationStatus}</p>
        <p><strong>Follow-up Count:</strong> {app.followUpSentCount}</p>
        {app.needFollowUp && <p className="badge-warn">Follow-up due</p>}
        {app.notes && <p><strong>Notes:</strong> {app.notes}</p>}
        <div className="actions">
          {app.followUpSentCount === 0 && (
            <button className="btn" onClick={() => markFollowUp.mutate()}>Mark Follow-up Sent</button>
          )}
          {app.followUpSentCount > 0 && (
            <button className="btn-secondary" onClick={() => resetFollowUp.mutate()}>Reset Follow-up</button>
          )}
        </div>
      </div>
    </div>
  );
}
