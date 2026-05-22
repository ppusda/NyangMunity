import { Link } from 'react-router-dom';
import { Sparkles, ArrowRight } from 'lucide-react';
import { useTopLikedImage } from '@/hooks/useImages';
import { Button } from '@/components/ui/button';

export function MainPage() {
  const { data: topLike } = useTopLikedImage();
  const hero = topLike?.imageInfo ?? null;

  return (
    <div className="px-8 py-16 max-w-6xl mx-auto">
      <div className="inline-flex items-center gap-2 text-[11px] font-medium uppercase tracking-[0.1em] text-text-tertiary mb-4">
        <Sparkles size={12} className="text-accent-bright" />
        NyangMunity · v1.0
      </div>
      <h1 className="font-display text-[44px] leading-[1.05] font-bold tracking-tight">
        고양이의 우아함을
        <br />
        한 자리에.
      </h1>
      <p className="mt-4 text-text-secondary text-base max-w-xl">
        좋아요를 모아 살아남은 사진만 영원히 보관됩니다.
        <br />
        오늘의 큐레이션부터 둘러보세요.
      </p>

      <div className="mt-8 flex gap-3">
        <Button asChild size="lg">
          <Link to="/posts">
            갤러리 둘러보기 <ArrowRight size={16} />
          </Link>
        </Button>
        <Button asChild variant="outline" size="lg">
          <Link to="/member/login">로그인</Link>
        </Button>
      </div>

      {hero && (
        <div className="mt-12 cat-card aspect-[16/9] max-w-3xl">
          <img src={hero.url} alt={hero.name ?? ''} />
          <div className="cc-overlay-bottom">
            <div className="cc-meta">
              <div className="text-lg font-semibold tracking-tight">
                {hero.name ?? '오늘의 1위'}
              </div>
              <div className="cc-meta-sub">가장 많은 좋아요를 받은 사진</div>
            </div>
            <div className="cc-stats">
              <span>♥ {hero.likesCount.toLocaleString()}</span>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
