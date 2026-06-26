interface PillBadgeProps {
  children: string;
}

export function PillBadge({ children }: PillBadgeProps) {
  return <span className="pill-badge">{children}</span>;
}
