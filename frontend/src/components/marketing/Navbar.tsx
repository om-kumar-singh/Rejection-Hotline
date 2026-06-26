import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useState } from 'react';
import { Button } from '../ui/Button';

const NAV_LINKS = [
  { label: 'Home', href: '#home' },
  { label: 'Features', href: '#features' },
  { label: 'Pricing', href: '#pricing' },
  { label: 'Contact', href: '#contact' },
];

function EnvelopeIcon() {
  return (
    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
      <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" />
      <polyline points="22,6 12,13 2,6" />
    </svg>
  );
}

export function Navbar() {
  const [menuOpen, setMenuOpen] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();
  const isLanding = location.pathname === '/';

  const handleAnchor = (href: string) => {
    setMenuOpen(false);
    if (isLanding) {
      document.querySelector(href)?.scrollIntoView({ behavior: 'smooth' });
    } else {
      navigate('/' + href);
    }
  };

  const scrollToRegister = () => {
    setMenuOpen(false);
    if (isLanding) {
      document.querySelector('#register')?.scrollIntoView({ behavior: 'smooth' });
    } else {
      navigate('/#register');
    }
  };

  return (
    <header className="marketing-nav">
      <div className="marketing-nav-inner">
        <Link to="/" className="marketing-logo" onClick={() => setMenuOpen(false)}>
          <span className="marketing-logo-icon">
            <EnvelopeIcon />
          </span>
          Rejection Hotline
        </Link>

        <nav className="marketing-nav-links" aria-label="Main navigation">
          {NAV_LINKS.map((link) => (
            <a
              key={link.href}
              href={link.href}
              onClick={(e) => {
                e.preventDefault();
                handleAnchor(link.href);
              }}
            >
              {link.label}
            </a>
          ))}
        </nav>

        <div className="marketing-nav-actions">
          <Link to="/login" className="ui-btn ui-btn--ghost">
            Login
          </Link>
          <Button variant="primary" onClick={scrollToRegister}>
            Get Started
          </Button>
          <button
            type="button"
            className="nav-toggle"
            aria-label="Toggle menu"
            aria-expanded={menuOpen}
            onClick={() => setMenuOpen((v) => !v)}
          >
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              {menuOpen ? (
                <>
                  <line x1="18" y1="6" x2="6" y2="18" />
                  <line x1="6" y1="6" x2="18" y2="18" />
                </>
              ) : (
                <>
                  <line x1="3" y1="12" x2="21" y2="12" />
                  <line x1="3" y1="6" x2="21" y2="6" />
                  <line x1="3" y1="18" x2="21" y2="18" />
                </>
              )}
            </svg>
          </button>
        </div>
      </div>

      <div className={`marketing-mobile-menu ${menuOpen ? 'open' : ''}`}>
        {NAV_LINKS.map((link) => (
          <a
            key={link.href}
            href={link.href}
            onClick={(e) => {
              e.preventDefault();
              handleAnchor(link.href);
            }}
          >
            {link.label}
          </a>
        ))}
        <Link to="/login" onClick={() => setMenuOpen(false)}>
          Login
        </Link>
      </div>
    </header>
  );
}
