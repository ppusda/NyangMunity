import { Heart, Eye } from 'lucide-react';
import { cn } from '@/lib/utils';
import { useLikeToggle } from '@/hooks/useLikeToggle';
import type { ImageItem } from '@/types';

interface Props {
  image: ImageItem;
  aspectRatio?: number;
  showViews?: boolean;
}

export function ImageCard({ image, aspectRatio, showViews }: Props) {
  const toggleLike = useLikeToggle();
  const liked = image.likeState ?? false;
  const thumbnail = image.thumbnailUrl ?? image.url;
  const title = image.name ?? '이름 없음';

  return (
    <div
      className="cat-card"
      style={aspectRatio ? { aspectRatio: `1 / ${aspectRatio}` } : undefined}
    >
      <img src={thumbnail} alt={title} loading="lazy" />

      <div className="cc-overlay-top">
        <span className="nm-chip nm-chip--micro">{labelForProvider(image.provider)}</span>
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
          <div className="cc-meta-title truncate">{title}</div>
        </div>
        <div className="cc-stats">
          <span>
            <Heart size={12} />
            {image.likesCount.toLocaleString()}
          </span>
          {showViews && (
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

function labelForProvider(p: ImageItem['provider']): string {
  switch (p) {
    case 'NYANGMUNITY':
      return '직접 업로드';
    case 'TENOR':
      return 'Tenor';
    case 'THECATAPI':
      return 'The Cat API';
    default:
      return p;
  }
}
