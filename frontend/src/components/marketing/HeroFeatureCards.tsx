const HERO_FEATURES = [
  {
    title: 'Import in minutes',
    description: 'Pull applications from Excel or Google Sheets without rebuilding your tracker.',
  },
  {
    title: 'Follow-ups on autopilot',
    description: 'Set a reminder once and get nudged when it is time to reach out again.',
  },
  {
    title: 'See what is working',
    description: 'Track response rates and spot which follow-ups actually get replies.',
  },
];

export function HeroFeatureCards() {
  return (
    <div className="hero-feature-cards">
      {HERO_FEATURES.map((feature) => (
        <div key={feature.title} className="hero-feature-card glass-card">
          <h3>{feature.title}</h3>
          <p>{feature.description}</p>
        </div>
      ))}
    </div>
  );
}
