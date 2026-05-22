import { Heart, Eye } from 'lucide-react';
import { cn } from '@/lib/utils';
import { useLikeToggle } from '@/hooks/useLikeToggle';
import type { ImageItem } from '@/types';

interface Props {
  items: ImageItem[];
  stats: {
    survived: number | null;
    joinedToday: number;
    mostFragileDays: number | null;
  };
}

const bentoSpans = ['tall', 'hero', 'sq1', 'sq2', 'wide', 'sq3', 'sq4'] as const;
type Span = (typeof bentoSpans)[number];

const spanGridStyle: Record<Span, React.CSSProperties> = {
  tall: { gridColumn: '1', gridRow: '1 / 3' },
  hero: { gridColumn: '2 / 4', gridRow: '1 / 3' },
  sq1: { gridColumn: '4', gridRow: '1' },
  sq2: { gridColumn: '4', gridRow: '2' },
  wide: { gridColumn: '1 / 3', gridRow: '3' },
  sq3: { gridColumn: '3', gridRow: '3' },
  sq4: { gridColumn: '4', gridRow: '3' },
};

export function CurationHero({ items, stats }: Props) {
  const today = new Date();
  const dateLabel = `${today.getFullYear()}.${String(today.getMonth() + 1).padStart(2, '0')}.${String(today.getDate()).padStart(2, '0')}`;
  const fragility = fragilityLevel(stats.mostFragileDays);

  const bentoItems = items.slice(0, 7);

  return (
    <section className="relative px-8 pt-9 pb-7 overflow-hidden border-b border-[var(--border-subtle)]">
      <div
        aria-hidden
        className="absolute inset-0 pointer-events-none"
        style={{
          background:
            'radial-gradient(ellipse 800px 400px at 80% 0%, rgba(122,122,154,0.18), transparent 70%)',
        }}
      />
      <div className="relative flex flex-col lg:flex-row lg:items-end lg:justify-between gap-6 mb-7">
        <div>
          <div className="inline-flex items-center gap-2 text-[11px] font-medium uppercase tracking-[0.1em] text-text-tertiary">
            <span
              className="w-1.5 h-1.5 rounded-full bg-accent-bright"
              style={{ boxShadow: '0 0 10px var(--accent-glow)' }}
            />
            오늘의 큐레이션 · {dateLabel}
          </div>
          <h1 className="font-display mt-3 text-[40px] leading-[1.05] font-bold tracking-tight">
            좋아요로 살아남은
            <br />
            오늘의 고양이.
          </h1>
          <p className="mt-3 text-sm text-text-secondary leading-relaxed">
            좋아요 50을 넘기면 영원히 보관됩니다.
            <br />
            그 아래는 90일 뒤 흩어집니다.
          </p>
        </div>

        <div className="flex items-stretch gap-6 px-5 py-4 rounded-lg border border-[var(--border-subtle)] bg-surface/60 backdrop-blur-sm">
          <StatItem label="살아남은 사진" value={stats.survived?.toLocaleString() ?? '—'} />
          <Divider />
          <StatItem label="오늘 합류" value={stats.joinedToday.toLocaleString()} />
          <Divider />
          <StatItem
            label="가장 위태로운 사진"
            value={
              stats.mostFragileDays !== null ? (
                <>
                  <span className="text-text-tertiary text-base font-medium align-top">D-</span>
                  <span className={cn(fragility.toneClass)}>{stats.mostFragileDays}</span>
                </>
              ) : (
                '—'
              )
            }
          />
        </div>
      </div>

      {bentoItems.length > 0 ? (
        <div
          className="ap-bento relative"
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(4, 1fr)',
            gridTemplateRows: '200px 200px 200px',
            gap: 12,
          }}
        >
          {bentoItems.map((img, i) => (
            <BentoTile key={img.id} image={img} span={bentoSpans[i]} hero={i === 1} />
          ))}
        </div>
      ) : (
        <div className="relative text-sm text-text-tertiary py-12 text-center border border-dashed border-[var(--border-default)] rounded-lg">
          큐레이션할 사진이 아직 없어요.
        </div>
      )}
    </section>
  );
}

function StatItem({ label, value }: { label: string; value: React.ReactNode }) {
  return (
    <div>
      <div className="text-2xl font-semibold tracking-tight font-en">{value}</div>
      <div className="text-[11px] text-text-tertiary mt-0.5">{label}</div>
    </div>
  );
}

function Divider() {
  return <div className="w-px self-stretch bg-[var(--border-subtle)]" />;
}

interface TileProps {
  image: ImageItem;
  span: Span;
  hero?: boolean;
}

function BentoTile({ image, span, hero }: TileProps) {
  const toggleLike = useLikeToggle();
  const liked = image.likeState ?? false;
  const thumbnail = image.url ?? image.thumbnailUrl ?? '';
  return (
    <div className={cn('cat-card ap-tile', `ap-tile--${span}`)} style={spanGridStyle[span]}>
      <img src={thumbnail} alt={image.name ?? ''} loading="lazy" />
      <div className="ap-tile-shade" />
      <div className="cc-overlay-top">
        {hero ? (
          <span
            className="nm-chip nm-chip--micro"
            style={{ color: 'var(--accent-bright)', borderColor: 'rgba(122,122,154,0.4)' }}
          >
            <span
              className="w-1.5 h-1.5 rounded-full bg-accent-bright mr-1"
              style={{ boxShadow: '0 0 8px var(--accent-glow)' }}
            />
            FEATURED
          </span>
        ) : (
          <span className="nm-chip nm-chip--micro">{image.likesCount.toLocaleString()} ♥</span>
        )}
        <button
          type="button"
          className={cn('cc-like-btn', liked && 'is-liked')}
          aria-label={liked ? '좋아요 취소' : '좋아요'}
          onClick={(e) => {
            e.preventDefault();
            e.stopPropagation();
            toggleLike.mutate(image.id);
          }}
        >
          <Heart size={14} fill={liked ? 'currentColor' : 'none'} />
        </button>
      </div>
      <div className="cc-overlay-bottom">
        <div className="cc-meta">
          <div className={cn(hero ? 'text-base font-semibold' : 'cc-meta-title', 'truncate')}>
            {image.name ?? '이름 없음'}
          </div>
        </div>
        <div className="cc-stats">
          <span>
            <Heart size={12} />
            {image.likesCount.toLocaleString()}
          </span>
          {hero && (
            <span>
              <Eye size={12} />
              {image.viewsCount.toLocaleString()}
            </span>
          )}
        </div>
      </div>
    </div>
  );
}

function fragilityLevel(days: number | null) {
  if (days === null) return { toneClass: 'text-text-primary' };
  if (days <= 7) return { toneClass: 'text-error' };
  if (days <= 30) return { toneClass: 'text-warning' };
  if (days <= 45) return { toneClass: 'text-warning' };
  return { toneClass: 'text-text-primary' };
}
