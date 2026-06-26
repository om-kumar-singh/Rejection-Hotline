import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authApi } from '../../api';
import { useAuthStore } from '../../store/authStore';
import { getApiErrorMessage } from '../../utils/apiError';
import { Button } from '../ui/Button';
import { FormField } from '../ui/FormField';
import { Input } from '../ui/Input';
import { PasswordInput } from '../ui/PasswordInput';

interface FieldErrors {
  fullName?: string;
  email?: string;
  password?: string;
  confirmPassword?: string;
}

function validateForm(
  fullName: string,
  email: string,
  password: string,
  confirmPassword: string
): FieldErrors {
  const errors: FieldErrors = {};
  if (!fullName.trim()) errors.fullName = 'Full name is required';
  if (!email.trim()) {
    errors.email = 'Email is required';
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
    errors.email = 'Enter a valid email address';
  }
  if (!password) {
    errors.password = 'Password is required';
  } else if (password.length < 8) {
    errors.password = 'Password must be at least 8 characters';
  }
  if (!confirmPassword) {
    errors.confirmPassword = 'Please confirm your password';
  } else if (password !== confirmPassword) {
    errors.confirmPassword = 'Passwords do not match';
  }
  return errors;
}

export function RegisterFormCard() {
  const navigate = useNavigate();
  const setAuth = useAuthStore((s) => s.setAuth);
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [fieldErrors, setFieldErrors] = useState<FieldErrors>({});
  const [apiError, setApiError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setApiError('');
    const errors = validateForm(fullName, email, password, confirmPassword);
    setFieldErrors(errors);
    if (Object.keys(errors).length > 0) return;

    setLoading(true);
    try {
      const { data } = await authApi.register({ fullName: fullName.trim(), email: email.trim(), password });
      setAuth(data.accessToken, data.refreshToken, data.user);
      navigate('/dashboard');
    } catch (err: unknown) {
      setApiError(getApiErrorMessage(err, 'Registration failed'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-form-card" id="register">
      <div className="auth-form-card-header">
        <h2>Create Your Account</h2>
        <p>Join thousands of job seekers using Rejection Hotline</p>
      </div>

      {apiError && (
        <div className="form-alert form-alert--error" role="alert">
          {apiError}
        </div>
      )}

      <form onSubmit={handleSubmit} noValidate>
        <FormField label="Full Name" htmlFor="reg-name" error={fieldErrors.fullName}>
          <Input
            id="reg-name"
            value={fullName}
            onChange={(e) => setFullName(e.target.value)}
            placeholder="Enter your full name"
            hasError={!!fieldErrors.fullName}
          />
        </FormField>

        <FormField label="Email Address" htmlFor="reg-email" error={fieldErrors.email}>
          <Input
            id="reg-email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="Enter your email address"
            hasError={!!fieldErrors.email}
          />
        </FormField>

        <FormField label="Password" htmlFor="reg-password" error={fieldErrors.password}>
          <PasswordInput
            id="reg-password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Create a strong password"
            hasError={!!fieldErrors.password}
          />
        </FormField>

        <FormField label="Confirm Password" htmlFor="reg-confirm" error={fieldErrors.confirmPassword}>
          <PasswordInput
            id="reg-confirm"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            placeholder="Confirm your password"
            hasError={!!fieldErrors.confirmPassword}
          />
        </FormField>

        <Button type="submit" variant="primary" block disabled={loading}>
          {loading ? (
            <>
              <span className="spinner" /> Creating account...
            </>
          ) : (
            'Create Account'
          )}
        </Button>
      </form>

      <p className="auth-form-footer">
        Already have an account? <Link to="/login">Login</Link>
      </p>
    </div>
  );
}
