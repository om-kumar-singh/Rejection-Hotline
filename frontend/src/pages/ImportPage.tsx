import { useState } from 'react';
import { useMutation } from '@tanstack/react-query';
import { importApi } from '../api';

export function ImportPage() {
  const [file, setFile] = useState<File | null>(null);
  const [preview, setPreview] = useState<Record<string, unknown> | null>(null);
  const [result, setResult] = useState<Record<string, unknown> | null>(null);
  const [spreadsheetId, setSpreadsheetId] = useState('');
  const [googleAuthUrl, setGoogleAuthUrl] = useState('');

  const previewMutation = useMutation({
    mutationFn: (f: File) => importApi.previewExcel(f).then((r) => r.data),
    onSuccess: setPreview,
  });

  const importMutation = useMutation({
    mutationFn: (f: File) => importApi.importExcel(f).then((r) => r.data),
    onSuccess: setResult,
  });

  const googleImportMutation = useMutation({
    mutationFn: () => importApi.importGoogleSheets(spreadsheetId).then((r) => r.data),
    onSuccess: setResult,
  });

  const connectGoogle = async () => {
    const { data } = await importApi.googleAuthUrl();
    setGoogleAuthUrl(data.authUrl);
    window.open(data.authUrl, '_blank');
  };

  return (
    <div>
      <div className="page-header">
        <h2>Import Applications</h2>
      </div>

      <section className="import-section">
        <h3>Excel Import (.xlsx)</h3>
        <p>Expected columns: HR Name, HR Email, Company Name, Job Role, Applied Date, Email Status, Reply Received, Follow-up Sent Count, Notes</p>
        <input type="file" accept=".xlsx" onChange={(e) => setFile(e.target.files?.[0] || null)} />
        <div className="actions">
          <button className="btn-secondary" disabled={!file} onClick={() => file && previewMutation.mutate(file)}>Preview</button>
          <button className="btn" disabled={!file} onClick={() => file && importMutation.mutate(file)}>Import</button>
        </div>
        {preview && <pre className="preview">{JSON.stringify(preview, null, 2)}</pre>}
        {result && <pre className="preview success">{JSON.stringify(result, null, 2)}</pre>}
      </section>

      <section className="import-section">
        <h3>Google Sheets Import</h3>
        <button className="btn-secondary" onClick={connectGoogle}>Connect Google Account</button>
        {googleAuthUrl && <p className="hint">Complete OAuth in the opened window, then import below.</p>}
        <label>Spreadsheet ID<input value={spreadsheetId} onChange={(e) => setSpreadsheetId(e.target.value)} placeholder="From Google Sheets URL" /></label>
        <button className="btn" disabled={!spreadsheetId} onClick={() => googleImportMutation.mutate()}>Import Sheet</button>
      </section>
    </div>
  );
}
