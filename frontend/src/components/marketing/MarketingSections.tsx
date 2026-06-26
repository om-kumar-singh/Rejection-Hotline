import { useState } from 'react';
import { Button } from '../ui/Button';

const FEATURES = [
  {
    icon: (
      <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z" />
        <polyline points="14 2 14 8 20 8" />
      </svg>
    ),
    title: 'Excel Import',
    description: 'Upload a spreadsheet and map your existing applications in one pass.',
  },
  {
    icon: (
      <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <rect x="3" y="3" width="18" height="18" rx="2" />
        <path d="M3 9h18M9 21V9" />
      </svg>
    ),
    title: 'Google Sheets Sync',
    description: 'Connect a sheet you already maintain and keep everything in sync.',
  },
  {
    icon: (
      <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <path d="M18 8A6 6 0 006 8c0 7-3 9-3 9h18s-3-2-3-9" />
        <path d="M13.73 21a2 2 0 01-3.46 0" />
      </svg>
    ),
    title: 'Smart Follow-up',
    description: 'Reminders land when a follow-up is due — not three days late.',
  },
  {
    icon: (
      <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <line x1="18" y1="20" x2="18" y2="10" />
        <line x1="12" y1="20" x2="12" y2="4" />
        <line x1="6" y1="20" x2="6" y2="14" />
      </svg>
    ),
    title: 'Dashboard Analytics',
    description: 'Response rates, pipeline counts, and company breakdowns at a glance.',
  },
  {
    icon: (
      <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" />
        <polyline points="22,6 12,13 2,6" />
      </svg>
    ),
    title: 'Email Templates',
    description: 'Reuse follow-up drafts so every message sounds professional, not rushed.',
  },
  {
    icon: (
      <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <rect x="3" y="11" width="18" height="11" rx="2" />
        <path d="M7 11V7a5 5 0 0110 0v4" />
      </svg>
    ),
    title: 'Secure Login',
    description: 'JWT-based auth keeps your job search data private to your account.',
  },
];

const STEPS = [
  { number: 1, title: 'Import Excel / Google Sheet', description: 'Bring in the applications you already track.' },
  { number: 2, title: 'Track Applications', description: 'Company, role, status, and dates in one place.' },
  { number: 3, title: 'Receive Follow-up Reminder', description: 'Get notified when silence has gone on too long.' },
  { number: 4, title: 'Increase Response Rate', description: 'Consistent follow-ups turn ghosted apps into replies.' },
];

const TESTIMONIALS = [
  {
    quote: 'I had 40 applications in a spreadsheet and zero follow-ups scheduled. Within a week I had replies from two companies I thought had ghosted me.',
    name: 'Priya Sharma',
    role: 'Software Engineer · Bangalore',
  },
  {
    quote: 'The import saved me hours. I just uploaded my Excel file and started getting reminders the next morning.',
    name: 'Marcus Chen',
    role: 'Product Manager · Remote',
  },
  {
    quote: 'Seeing my response rate climb month over month kept me motivated during a brutal job market.',
    name: 'Elena Rodriguez',
    role: 'Data Analyst · Austin',
  },
];

const FAQ_ITEMS = [
  {
    question: 'Is Rejection Hotline really free?',
    answer: 'Yes. The core tracker, reminders, and analytics are free while we grow the product with early users.',
  },
  {
    question: 'Can I import my existing spreadsheet?',
    answer: 'Absolutely. Upload an Excel file or connect Google Sheets — we map the standard columns for you.',
  },
  {
    question: 'How do follow-up reminders work?',
    answer: 'Set a follow-up date on any application. We notify you when it is due so you can send a timely nudge.',
  },
  {
    question: 'Is my data secure?',
    answer: 'Your account is protected with JWT authentication. Only you can access your applications and analytics.',
  },
];

function FaqAccordion() {
  const [openIndex, setOpenIndex] = useState<number | null>(0);

  return (
    <div className="faq-list">
      {FAQ_ITEMS.map((item, index) => {
        const isOpen = openIndex === index;
        return (
          <div key={item.question} className={`faq-item glass-card ${isOpen ? 'open' : ''}`}>
            <button
              type="button"
              className="faq-question"
              aria-expanded={isOpen}
              onClick={() => setOpenIndex(isOpen ? null : index)}
            >
              <span>{item.question}</span>
              <span className="faq-icon">{isOpen ? '−' : '+'}</span>
            </button>
            {isOpen && <p className="faq-answer">{item.answer}</p>}
          </div>
        );
      })}
    </div>
  );
}

export function MarketingSections() {
  const scrollToRegister = () => {
    document.querySelector('#register')?.scrollIntoView({ behavior: 'smooth' });
  };

  return (
    <>
      <section className="marketing-section marketing-section--alt" id="how-it-works">
        <div className="marketing-section-inner">
          <div className="section-header">
            <h2>How It Works</h2>
            <p>From messy spreadsheet to consistent follow-ups in four steps.</p>
          </div>
          <div className="workflow-steps">
            {STEPS.map((step, index) => (
              <div key={step.number} className="workflow-step-wrap">
                <div className="workflow-step glass-card">
                  <span className="workflow-step-num">Step {step.number}</span>
                  <h3>{step.title}</h3>
                  <p>{step.description}</p>
                </div>
                {index < STEPS.length - 1 && <div className="workflow-arrow" aria-hidden="true">↓</div>}
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="marketing-section" id="features">
        <div className="marketing-section-inner">
          <div className="section-header">
            <h2>Built for the job search grind</h2>
            <p>Everything you need to stay organized without another generic productivity app.</p>
          </div>
          <div className="features-grid">
            {FEATURES.map((feature) => (
              <div key={feature.title} className="feature-card glass-card">
                <div className="feature-card-icon">{feature.icon}</div>
                <h3>{feature.title}</h3>
                <p>{feature.description}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="marketing-section marketing-section--alt" id="testimonials">
        <div className="marketing-section-inner">
          <div className="section-header">
            <h2>What job seekers are saying</h2>
            <p>Real workflows from people who were tired of losing track.</p>
          </div>
          <div className="testimonials-grid">
            {TESTIMONIALS.map((t) => (
              <blockquote key={t.name} className="testimonial-card glass-card">
                <p>&ldquo;{t.quote}&rdquo;</p>
                <footer>
                  <strong>{t.name}</strong>
                  <span>{t.role}</span>
                </footer>
              </blockquote>
            ))}
          </div>
        </div>
      </section>

      <section className="marketing-section" id="pricing">
        <div className="marketing-section-inner">
          <div className="section-header">
            <h2>Pricing</h2>
            <p>No tiers, no tricks — just start tracking.</p>
          </div>
          <div className="pricing-card glass-card">
            <h3>Free for job seekers</h3>
            <div className="pricing-price">$0</div>
            <ul>
              <li>Unlimited applications</li>
              <li>Follow-up reminders</li>
              <li>Excel &amp; Google Sheets import</li>
              <li>Analytics dashboard</li>
            </ul>
            <Button variant="primary" block onClick={scrollToRegister}>
              Start Tracking Free
            </Button>
          </div>
        </div>
      </section>

      <section className="marketing-section marketing-section--alt" id="faq">
        <div className="marketing-section-inner">
          <div className="section-header">
            <h2>Frequently asked questions</h2>
          </div>
          <FaqAccordion />
        </div>
      </section>

      <section className="marketing-section" id="contact">
        <div className="marketing-section-inner">
          <div className="contact-block glass-card">
            <h2>Questions before you start?</h2>
            <p>We read every message. Reach out and we will get back within a business day.</p>
            <a href="mailto:support@rejectionhotline.com">support@rejectionhotline.com</a>
          </div>
        </div>
      </section>
    </>
  );
}
