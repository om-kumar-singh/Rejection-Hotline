import { useEffect } from 'react';
import heroImg from '../assets/hero-illustration.png';
import { HeroFeatureCards } from '../components/marketing/HeroFeatureCards';
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
      <section className="hero-section hero-section--split" id="home">
        <div className="hero-copy">
          <PillBadge>For job seekers who hate losing track</PillBadge>

          <h1 className="hero-headline">
            Never forget to follow up on another job application.
          </h1>

          <p className="hero-subtitle">
            Rejection Hotline turns your application list into a follow-up system — import what
            you have, get reminded when to reach out, and stop letting opportunities go cold.
          </p>

          <div className="hero-cta-row">
            <Button variant="primary" onClick={scrollToRegister}>
              Start Tracking Free
            </Button>
            <Button variant="ghost" onClick={scrollToHowItWorks}>
              Learn More
            </Button>
          </div>

          <HeroFeatureCards />
        </div>

        <div className="hero-visual">
          <div className="hero-image-frame">
            <img
              src={heroImg}
              alt="Job seeker tracking applications and follow-ups"
              className="hero-image"
            />
          </div>
        </div>
      </section>

      <section className="register-section">
        <div className="register-section-inner">
          <RegisterFormCard />
        </div>
      </section>

      <MarketingSections />
    </MarketingLayout>
  );
}
