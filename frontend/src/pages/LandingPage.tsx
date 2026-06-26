import { useEffect } from 'react';
import { HeroFeatures } from '../components/marketing/HeroFeatures';
import { MarketingSections } from '../components/marketing/MarketingSections';
import { RegisterFormCard } from '../components/marketing/RegisterFormCard';
import { Button } from '../components/ui/Button';
import { PillBadge } from '../components/ui/PillBadge';
import { MarketingLayout } from '../layouts/MarketingLayout';

export function LandingPage() {
  useEffect(() => {
    const hash = window.location.hash;
    if (hash) {
      setTimeout(() => document.querySelector(hash)?.scrollIntoView({ behavior: 'smooth' }), 100);
    }
  }, []);

  const scrollToRegister = () => {
    document.querySelector('#register')?.scrollIntoView({ behavior: 'smooth' });
  };

  const scrollToHowItWorks = () => {
    document.querySelector('#how-it-works')?.scrollIntoView({ behavior: 'smooth' });
  };

  return (
    <MarketingLayout>
      <section className="hero-section hero-section--grid" id="home">
        <div className="hero-grid">
          <div className="hero-copy">
            <PillBadge>Track. Follow-up. Get Responses.</PillBadge>

            <h1 className="hero-headline">
              Never Lose Track of an <span className="accent">Opportunity</span> Again.
            </h1>

            <p className="hero-subtitle">
              Rejection Hotline helps you track job applications, schedule follow-ups, and boost
              your chances of getting that reply.
            </p>

            <HeroFeatures />

            <div className="hero-cta-row">
              <Button variant="primary" onClick={scrollToRegister}>
                Get Started for Free →
              </Button>
              <Button variant="ghost" onClick={scrollToHowItWorks}>
                Learn More
              </Button>
            </div>

            <p className="hero-microcopy">No credit card required. Start tracking in seconds.</p>
          </div>

          <div className="hero-form">
            <RegisterFormCard />
          </div>
        </div>
      </section>

      <MarketingSections />
    </MarketingLayout>
  );
}
