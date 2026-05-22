import { cn } from '@/lib/utils';
import type { Provider } from '@/types';

interface Props {
  providers: Provider[];
  selected: Provider | undefined;
  onSelect: (provider: Provider | undefined) => void;
}

const providerLabels: Record<Provider, string> = {
  NYANGMUNITY: '직접 업로드',
  TENOR: 'Tenor',
  THECATAPI: 'The Cat API',
};

export function BreedChips({ providers, selected, onSelect }: Props) {
  return (
    <div className="flex gap-2 overflow-x-auto nm-scroll py-1 -mx-1 px-1">
      <button
        type="button"
        className={cn('nm-chip', selected === undefined && 'is-on')}
        onClick={() => onSelect(undefined)}
      >
        전체
      </button>
      {providers.map((p) => (
        <button
          key={p}
          type="button"
          className={cn('nm-chip', selected === p && 'is-on')}
          onClick={() => onSelect(p)}
        >
          {providerLabels[p] ?? p}
        </button>
      ))}
    </div>
  );
}
