import type { ReactNode } from 'react';
import { Footer } from '../components/marketing/Footer';
import { Navbar } from '../components/marketing/Navbar';

interface MarketingLayoutProps {
  children: ReactNode;
}

export function MarketingLayout({ children }: MarketingLayoutProps) {
  return (
    <div className="marketing-page">
      <Navbar />
      <main className="marketing-main">{children}</main>
      <Footer />
    </div>
  );
}
