import { useEffect, useMemo } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import {
  ChevronLeft,
  ChevronRight,
  ChevronsLeft,
  Eye,
  Flag,
  Heart,
  Maximize2,
  MessageSquareText,
  Share2,
  Bookmark,
  Sparkles,
} from 'lucide-react';
import { toast } from 'sonner';
import { useImageDetail, useGalleryNeighbors } from '@/hooks/useImageDetail';
import { usePopularImages } from '@/hooks/useImages';
import { useLikeToggle } from '@/hooks/useLikeToggle';
import { useAuthStore } from '@/stores/authStore';
import { useUIStore } from '@/stores/uiStore';
import { cn } from '@/lib/utils';
import type { ImageDetailResponse } from '@/types';

const SURVIVAL_THRESHOLD = 50;

function providerLabel(p: ImageDetailResponse['provider']): string {
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

function formatUploadDate(dateStr: string | undefined): string {
  if (!dateStr) return '';
  const d = new Date(dateStr);
  if (Number.isNaN(d.getTime())) return '';
  const now = Date.now();
  const diff = now - d.getTime();
  const day = 24 * 60 * 60 * 1000;
  if (diff < day) return '오늘';
  if (diff < 2 * day) return '어제';
  if (diff < 30 * day) return `${Math.floor(diff / day)}일 전`;
  return d.toLocaleDateString('ko-KR', { year: 'numeric', month: '2-digit', day: '2-digit' });
}

function dDayFromExpires(expiresAt?: string | null): number | null {
  if (!expiresAt) return null;
  const exp = new Date(expiresAt).getTime();
  if (Number.isNaN(exp)) return null;
  return Math.max(0, Math.ceil((exp - Date.now()) / (24 * 60 * 60 * 1000)));
}

export function ImageDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const isLogin = useAuthStore((s) => s.isLogin);
  const openAuthPrompt = useUIStore((s) => s.openAuthPrompt);
  const { data: image, isLoading, isError } = useImageDetail(id);
  const { prev, next, position } = useGalleryNeighbors(id);
  const toggleLike = useLikeToggle();

  const { data: similarSource = [] } = usePopularImages(8);
  const similar = useMemo(
    () => similarSource.filter((s) => s.id !== id).slice(0, 5),
    [similarSource, id],
  );

  useEffect(() => {
    if (!image) return;
    document.title = `${image.name ?? '이미지'} · NyangMunity`;
    return () => {
      document.title = 'NyangMunity';
    };
  }, [image]);

  if (isLoading) {
    return (
      <div className="h-screen flex items-center justify-center bg-background text-text-tertiary text-sm">
        이미지 정보를 불러오는 중...
      </div>
    );
  }

  if (isError || !image) {
    return (
      <div className="h-screen flex flex-col items-center justify-center bg-background gap-4 px-6 text-center">
        <p className="text-text-secondary text-sm">이미지를 찾을 수 없어요.</p>
        <button
          type="button"
          className="nm-btn nm-btn--outline"
          onClick={() => navigate('/posts')}
        >
          <ChevronsLeft size={16} /> 갤러리로 돌아가기
        </button>
      </div>
    );
  }

  const likeCount = image.likesCount;
  const tags = image.tags ?? [];
  const survived = likeCount >= SURVIVAL_THRESHOLD;
  const pct = Math.min(100, (likeCount / SURVIVAL_THRESHOLD) * 100);
  const dDay = dDayFromExpires(image.expiresAt);
  const liked = image.likeState ?? false;
  const uploader = image.uploader ?? null;
  const uploaderInitial = uploader?.[0] ?? '?';

  const handleLike = () => {
    if (!isLogin) {
      openAuthPrompt('좋아요는 로그인 후 이용할 수 있어요.');
      return;
    }
    toggleLike.mutate(image.id);
  };

  const handleShare = async () => {
    const url = window.location.href;
    try {
      await navigator.clipboard.writeText(url);
      toast.success('링크를 복사했어요.');
    } catch {
      toast.error('복사에 실패했어요. 직접 주소를 복사해주세요.');
    }
  };

  const goPrev = () => {
    if (prev) navigate(`/images/${prev}`);
  };
  const goNext = () => {
    if (next) navigate(`/images/${next}`);
  };

  return (
    <div className="h-screen flex flex-col bg-background text-text-primary">
      <header className="d2-topbar">
        <div className="d2-topbar-left">
          <button
            type="button"
            className="nm-btn nm-btn--ghost nm-btn--sm"
            onClick={() => navigate(-1)}
          >
            <ChevronLeft size={16} /> 갤러리로
          </button>
          <span className="d2-divider" aria-hidden="true" />
          <div className="d2-crumb">
            <Link to="/posts" className="hover:text-text-primary transition-colors">
              갤러리
            </Link>
            <ChevronRight />
            <span>{providerLabel(image.provider)}</span>
            <ChevronRight />
            <span className="d2-crumb-current truncate max-w-[200px]">
              {image.name ?? '이름 없음'}
            </span>
          </div>
        </div>
        <div className="d2-topbar-right">
          {position && (
            <span className="d2-pos">
              {position.index} / {position.total}
            </span>
          )}
          <button
            type="button"
            className="nm-btn nm-btn--ghost nm-btn--icon"
            aria-label="이전 사진"
            onClick={goPrev}
            disabled={!prev}
          >
            <ChevronLeft size={16} />
          </button>
          <button
            type="button"
            className="nm-btn nm-btn--ghost nm-btn--icon"
            aria-label="다음 사진"
            onClick={goNext}
            disabled={!next}
          >
            <ChevronRight size={16} />
          </button>
        </div>
      </header>

      <div className="d2-body">
        <section className="d2-image-col">
          <div className="d2-image-frame">
            <img src={image.url} alt={image.name ?? '고양이 사진'} />
          </div>
          <div className="d2-image-foot">
            <div className="d2-image-foot-left">
              <span className="nm-chip nm-chip--sm d2-breed">
                {providerLabel(image.provider)}
              </span>
              <span className="d2-stat">
                <Heart size={13} /> {likeCount.toLocaleString()}
              </span>
              <span className="d2-stat">
                <Eye size={13} /> {image.viewsCount.toLocaleString()}
              </span>
            </div>
            <div className="d2-image-foot-right">
              <button
                type="button"
                className="nm-btn nm-btn--ghost nm-btn--sm"
                onClick={handleShare}
              >
                <Share2 size={14} /> 공유
              </button>
              <a
                className="nm-btn nm-btn--ghost nm-btn--sm"
                href={image.url}
                target="_blank"
                rel="noreferrer noopener"
              >
                <Maximize2 size={14} /> 원본
              </a>
            </div>
          </div>
        </section>

        <aside className="d2-info-col nm-scroll">
          <section className="d2-section">
            <div className="dd-author">
              <div className="dd-author-avatar">{uploaderInitial}</div>
              <div className="dd-author-meta">
                <div className="dd-author-name">{uploader ?? '익명의 집사'}</div>
                <div className="dd-author-handle">
                  {formatUploadDate(image.uploadDate)} 업로드
                </div>
              </div>
            </div>
          </section>

          <section className="d2-section d2-section--title">
            <h1 className="d2-title">{image.name ?? '이름 없는 고양이'}</h1>
            <div className="d2-subtitle">
              <span className="d2-subtitle-time">
                {providerLabel(image.provider)}
              </span>
            </div>
            {image.description && (
              <p className="d2-caption">{image.description}</p>
            )}
            {tags.length > 0 && (
              <div className="dd-tags">
                {tags.map((t) => (
                  <span key={t.id} className="dd-tag">
                    #{t.name}
                  </span>
                ))}
              </div>
            )}
          </section>

          <section className="d2-section">
            <div className="dd-survival">
              <div className="dd-survival-head">
                {survived ? (
                  <span className="dd-survival-status dd-survival-status--saved">
                    <Sparkles size={14} /> 영구 보관됨
                  </span>
                ) : (
                  <span className="dd-survival-status">
                    영구 보관까지{' '}
                    <em>{SURVIVAL_THRESHOLD - likeCount}</em>개
                  </span>
                )}
                <span className="dd-survival-num">
                  {likeCount.toLocaleString()}
                  {!survived && ` / ${SURVIVAL_THRESHOLD}`}
                  {survived && <em> likes</em>}
                </span>
              </div>
              <div className="dd-survival-track">
                <div
                  className={cn('dd-survival-fill', survived && 'is-survived')}
                  style={{ width: `${pct}%` }}
                />
              </div>
              {!survived && (
                <div className="dd-survival-meta">
                  {dDay !== null && <span className="dd-dday">D-{dDay}</span>}
                  <span>좋아요 50개를 받으면 영원히 남습니다.</span>
                </div>
              )}
            </div>
          </section>

          <section className="d2-section">
            <div className="dd-actions">
              <button
                type="button"
                className={cn('dd-action dd-action--like', liked && 'is-on')}
                onClick={handleLike}
              >
                <Heart size={16} fill={liked ? 'currentColor' : 'none'} />
                <span>{liked ? '좋아요 취소' : '좋아요'}</span>
                <span className="dd-action-count">
                  {likeCount.toLocaleString()}
                </span>
              </button>
              <button
                type="button"
                className="dd-action"
                disabled
                title="저장 기능은 곧 추가됩니다"
              >
                <Bookmark size={16} />
                <span>저장</span>
              </button>
              <button
                type="button"
                className="dd-action"
                onClick={handleShare}
              >
                <Share2 size={16} />
                <span>공유</span>
              </button>
              <button
                type="button"
                className="dd-action dd-action--muted"
                disabled
                aria-label="신고 (준비 중)"
              >
                <Flag size={16} />
              </button>
            </div>
          </section>

          <div className="d2-divider-rule" />

          <section className="d2-section">
            <div className="dd-comments-placeholder">
              <strong>
                <MessageSquareText
                  size={14}
                  style={{ display: 'inline', verticalAlign: '-2px', marginRight: 6 }}
                />
                댓글
              </strong>
              댓글 기능은 v1.5에서 추가됩니다.
            </div>
          </section>

          {similar.length > 0 && (
            <>
              <div className="d2-divider-rule" />
              <section className="d2-section">
                <div className="dd-similar">
                  <div className="dd-similar-head">
                    <h3 className="dd-similar-title">
                      <Sparkles size={14} />
                      비슷한 인기 사진
                    </h3>
                    <Link to="/posts" className="text-xs text-text-secondary hover:text-accent-bright">
                      전체 보기
                    </Link>
                  </div>
                  <div className="dd-similar-grid">
                    {similar.map((s) => (
                      <Link
                        key={s.id}
                        to={`/images/${s.id}`}
                        className="dd-similar-tile"
                      >
                        <img src={s.thumbnailUrl ?? s.url} alt={s.name ?? ''} loading="lazy" />
                        <div className="dd-similar-likes">
                          <Heart size={10} />
                          {s.likesCount.toLocaleString()}
                        </div>
                      </Link>
                    ))}
                  </div>
                </div>
              </section>
            </>
          )}
        </aside>
      </div>

    </div>
  );
}
