import { useEffect, useMemo, useRef } from 'react';
import { ImageCard } from './ImageCard';
import type { ImageItem } from '@/types';

interface Props {
  items: ImageItem[];
  onReachEnd?: () => void;
  isLoadingMore?: boolean;
  hasMore?: boolean;
}

const COLUMN_COUNT = 3;

export function MasonryGrid({ items, onReachEnd, isLoadingMore, hasMore }: Props) {
  const sentinelRef = useRef<HTMLDivElement | null>(null);

  const columns = useMemo(() => {
    const cols: ImageItem[][] = Array.from({ length: COLUMN_COUNT }, () => []);
    items.forEach((item, idx) => {
      cols[idx % COLUMN_COUNT].push(item);
    });
    return cols;
  }, [items]);

  useEffect(() => {
    if (!onReachEnd || !sentinelRef.current) return;
    const el = sentinelRef.current;
    const observer = new IntersectionObserver(
      (entries) => {
        const e = entries[0];
        if (e?.isIntersecting && hasMore && !isLoadingMore) {
          onReachEnd();
        }
      },
      { rootMargin: '400px' },
    );
    observer.observe(el);
    return () => observer.disconnect();
  }, [onReachEnd, hasMore, isLoadingMore]);

  return (
    <>
      <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
        {columns.map((col, ci) => (
          <div key={ci} className="flex flex-col gap-4">
            {col.map((item) => (
              <ImageCard
                key={item.id}
                image={item}
                aspectRatio={pseudoAspect(item.id)}
                showViews
              />
            ))}
          </div>
        ))}
      </div>
      <div ref={sentinelRef} className="h-1" />
      {isLoadingMore && (
        <div className="text-center text-sm text-text-tertiary py-6">불러오는 중...</div>
      )}
      {!hasMore && items.length > 0 && (
        <div className="text-center text-xs text-text-tertiary py-6">
          더 이상 사진이 없습니다.
        </div>
      )}
    </>
  );
}

function pseudoAspect(seed: string): number {
  let hash = 0;
  for (let i = 0; i < seed.length; i++) {
    hash = (hash * 31 + seed.charCodeAt(i)) & 0xffffffff;
  }
  const buckets = [1.0, 1.25, 1.4, 1.6, 0.85];
  return buckets[Math.abs(hash) % buckets.length];
}
