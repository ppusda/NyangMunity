import { cn } from '@/lib/utils';
import type { SortKey } from '@/types';

interface Props {
  value: SortKey;
  onChange: (v: SortKey) => void;
}

const tabs: Array<{ key: SortKey; label: string }> = [
  { key: 'latest', label: '최신' },
  { key: 'likes', label: '인기' },
  { key: 'views', label: '조회순' },
];

export function SortTabs({ value, onChange }: Props) {
  return (
    <div className="inline-flex items-center bg-surface border border-[var(--border-subtle)] rounded-md p-0.5 gap-0.5">
      {tabs.map((t) => (
        <button
          key={t.key}
          type="button"
          onClick={() => onChange(t.key)}
          className={cn(
            'h-7 px-3 rounded text-[13px] font-medium transition-colors',
            value === t.key
              ? 'bg-elevated text-text-primary'
              : 'text-text-secondary hover:text-text-primary',
          )}
        >
          {t.label}
        </button>
      ))}
    </div>
  );
}
