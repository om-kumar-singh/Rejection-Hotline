const FEATURES = [
  {
    icon: (
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <path d="M16 4h2a2 2 0 012 2v14a2 2 0 01-2 2H6a2 2 0 01-2-2V6a2 2 0 012-2h2" />
        <rect x="8" y="2" width="8" height="4" rx="1" ry="1" />
      </svg>
    ),
    title: 'Track Applications',
    description: 'Keep all your job applications in one place.',
  },
  {
    icon: (
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <path d="M18 8A6 6 0 006 8c0 7-3 9-3 9h18s-3-2-3-9" />
        <path d="M13.73 21a2 2 0 01-3.46 0" />
      </svg>
    ),
    title: 'Smart Reminders',
    description: "Get reminded when it's time to follow up.",
  },
  {
    icon: (
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <line x1="18" y1="20" x2="18" y2="10" />
        <line x1="12" y1="20" x2="12" y2="4" />
        <line x1="6" y1="20" x2="6" y2="14" />
      </svg>
    ),
    title: 'Analyze & Improve',
    description: 'Visualize your progress and response rate.',
  },
];

export function HeroFeatures() {
  return (
    <div className="hero-features">
      {FEATURES.map((feature) => (
        <div key={feature.title} className="hero-feature-item">
          <div className="hero-feature-icon">{feature.icon}</div>
          <div>
            <strong>{feature.title}</strong>
            <span>{feature.description}</span>
          </div>
        </div>
      ))}
    </div>
  );
}
