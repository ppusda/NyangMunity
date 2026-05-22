import { useEffect } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { X, Heart } from 'lucide-react';

interface Props {
  open: boolean;
  onClose: () => void;
  message?: string;
}

export function AuthPromptModal({ open, onClose, message }: Props) {
  const location = useLocation();

  useEffect(() => {
    if (!open) return;
    const onKey = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };
    window.addEventListener('keydown', onKey);
    document.body.style.overflow = 'hidden';
    return () => {
      window.removeEventListener('keydown', onKey);
      document.body.style.overflow = '';
    };
  }, [open, onClose]);

  if (!open) return null;

  const from = location.pathname + location.search;

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center px-6"
      role="dialog"
      aria-modal="true"
      aria-labelledby="auth-prompt-title"
    >
      <div
        className="absolute inset-0 bg-black/70 backdrop-blur-sm"
        onClick={onClose}
      />

      <div className="relative w-full max-w-sm rounded-[16px] border border-[var(--border-default)] bg-elevated p-6 shadow-2xl">
        <button
          type="button"
          onClick={onClose}
          aria-label="닫기"
          className="absolute right-3 top-3 text-text-tertiary hover:text-text-primary p-1.5 rounded-md hover:bg-white/[0.06] transition-colors"
        >
          <X size={16} />
        </button>

        <div className="flex flex-col items-center text-center">
          <div className="w-12 h-12 rounded-full bg-[rgba(239,68,68,0.12)] flex items-center justify-center mb-4">
            <Heart size={22} className="text-[var(--like)]" />
          </div>
          <h2 id="auth-prompt-title" className="text-lg font-semibold tracking-tight">
            로그인이 필요해요
          </h2>
          <p className="text-sm text-text-secondary mt-2">
            {message ?? '계속하려면 로그인하거나 가입해주세요.'}
          </p>

          <div className="w-full mt-6 space-y-2">
            <Link
              to="/member/login"
              state={{ from }}
              className="nm-btn nm-btn--primary nm-btn--lg w-full"
              onClick={onClose}
            >
              로그인
            </Link>
            <Link
              to="/member/join"
              className="nm-btn nm-btn--outline nm-btn--lg w-full"
              onClick={onClose}
            >
              회원가입
            </Link>
          </div>

          <button
            type="button"
            onClick={onClose}
            className="mt-4 text-xs text-text-tertiary hover:text-text-secondary transition-colors"
          >
            나중에 할게요
          </button>
        </div>
      </div>
    </div>
  );
}
