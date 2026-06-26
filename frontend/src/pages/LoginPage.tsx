import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Button } from '../components/ui/Button';
import { FormField } from '../components/ui/FormField';
import { Input } from '../components/ui/Input';
import { PasswordInput } from '../components/ui/PasswordInput';
import { PillBadge } from '../components/ui/PillBadge';
import { authApi } from '../api';
import { useAuthStore } from '../store/authStore';
import { getApiErrorMessage } from '../utils/apiError';
import { MarketingLayout } from '../layouts/MarketingLayout';

export function LoginPage() {
  const navigate = useNavigate();
  const setAuth = useAuthStore((s) => s.setAuth);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const { data } = await authApi.login({ email, password });
      setAuth(data.accessToken, data.refreshToken, data.user);
      navigate('/dashboard');
    } catch (err: unknown) {
      setError(getApiErrorMessage(err, 'Invalid email or password'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <MarketingLayout>
      <section className="hero-section hero-section--auth" id="home">
        <div className="hero-copy">
          <PillBadge>Welcome back</PillBadge>
          <h1 className="hero-headline">Pick up where you left off.</h1>
          <p className="hero-subtitle">
            Your applications, follow-up reminders, and response stats are waiting. Sign in and
            keep the momentum going.
          </p>
        </div>

        <div className="auth-form-card">
          <div className="auth-form-card-header">
            <h2>Sign In</h2>
            <p>Access your Rejection Hotline account</p>
          </div>

          {error && (
            <div className="form-alert form-alert--error" role="alert">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} noValidate>
            <FormField label="Email Address" htmlFor="login-email">
              <Input
                id="login-email"
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="Enter your email address"
                required
              />
            </FormField>

            <FormField label="Password" htmlFor="login-password">
              <PasswordInput
                id="login-password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Enter your password"
                required
              />
            </FormField>

            <Button type="submit" variant="primary" block disabled={loading}>
              {loading ? (
                <>
                  <span className="spinner" /> Signing in...
                </>
              ) : (
                'Login'
              )}
            </Button>
          </form>

          <p className="auth-form-footer">
            No account yet? <Link to="/#register">Create one for free</Link>
          </p>
        </div>
      </section>
    </MarketingLayout>
  );
}
