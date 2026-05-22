import { useLocation } from 'react-router-dom';
import { Bell, ChevronRight, Search, Settings } from 'lucide-react';
import { useAuthStore } from '@/stores/authStore';

const routeLabels: Record<string, string> = {
  '/': '홈',
  '/posts': '갤러리',
  '/member/info': '마이페이지',
};

export function Topbar() {
  const { pathname } = useLocation();
  const member = useAuthStore((s) => s.member);
  const label = routeLabels[pathname] ?? pathname;

  return (
    <header className="h-[60px] flex items-center justify-between px-6 border-b border-[var(--border-subtle)] bg-background/80 backdrop-blur-md sticky top-0 z-10">
      <div className="flex items-center gap-2 text-sm">
        <span className="text-text-tertiary">NyangMunity</span>
        <ChevronRight size={14} className="text-text-tertiary" />
        <span className="text-text-primary font-medium">{label}</span>
      </div>

      <div className="flex-1 max-w-md mx-8">
        <div className="nm-input-wrap">
          <Search />
          <input
            className="nm-input"
            placeholder="고양이·품종·태그 검색 (준비 중)"
            disabled
          />
          <span className="nm-kbd">⌘ K</span>
        </div>
      </div>

      <div className="flex items-center gap-2">
        <button
          type="button"
          className="nm-btn nm-btn--ghost nm-btn--icon"
          aria-label="알림"
          disabled
        >
          <Bell size={16} />
        </button>
        <button
          type="button"
          className="nm-btn nm-btn--ghost nm-btn--icon"
          aria-label="설정"
          disabled
        >
          <Settings size={16} />
        </button>
        {member && (
          <div className="w-8 h-8 rounded-full bg-elevated flex items-center justify-center text-sm font-semibold ml-1">
            {member.nickname?.[0] ?? '?'}
          </div>
        )}
      </div>
    </header>
  );
}
