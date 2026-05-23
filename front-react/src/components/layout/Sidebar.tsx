import { NavLink } from 'react-router-dom';
import {
  Images,
  MessageSquareText,
  Heart,
  Bookmark,
  PanelLeftClose,
  PanelLeftOpen,
  Upload,
  ChevronsUpDown,
  LogIn,
} from 'lucide-react';
import { NmLogo } from '@/components/icons/NmLogoMark';
import { useAuthStore } from '@/stores/authStore';
import { useUIStore } from '@/stores/uiStore';
import { cn } from '@/lib/utils';

interface Props {
  collapsed: boolean;
  onToggle: () => void;
}

const navItems = [
  { to: '/posts', icon: Images, label: '갤러리' },
  { to: '/community', icon: MessageSquareText, label: '커뮤니티', disabled: true },
  { to: '/mine', icon: Heart, label: '내 컬렉션', disabled: true, badge: '24' },
  { to: '/bookmark', icon: Bookmark, label: '저장', disabled: true },
];

export function Sidebar({ collapsed, onToggle }: Props) {
  const member = useAuthStore((s) => s.member);
  const isLogin = useAuthStore((s) => s.isLogin);
  const openUpload = useUIStore((s) => s.openUpload);
  const openAuthPrompt = useUIStore((s) => s.openAuthPrompt);

  const handleUploadClick = () => {
    if (!isLogin) {
      openAuthPrompt('사진 업로드는 로그인 후 이용할 수 있어요.');
      return;
    }
    openUpload();
  };

  return (
    <aside
      className={cn(
        'h-screen flex flex-col border-r border-[var(--border-subtle)] bg-surface transition-[width] duration-200 ease-out shrink-0',
        collapsed ? 'w-[60px]' : 'w-[244px]',
      )}
    >
      <div
        className={cn(
          'flex items-center h-[60px] border-b border-[var(--border-subtle)] shrink-0',
          collapsed ? 'justify-center' : 'justify-between px-3',
        )}
      >
        {!collapsed && <NmLogo />}
        <button
          type="button"
          onClick={onToggle}
          className="text-text-secondary hover:text-text-primary p-1.5 rounded-md hover:bg-white/[0.04] transition-colors"
          aria-label={collapsed ? '사이드바 펼치기' : '사이드바 접기'}
        >
          {collapsed ? <PanelLeftOpen size={16} /> : <PanelLeftClose size={16} />}
        </button>
      </div>

      <div className={cn('pt-4', collapsed ? 'px-0 flex justify-center' : 'px-3')}>
        <button
          type="button"
          onClick={handleUploadClick}
          title="사진 올리기"
          aria-label="사진 올리기"
          className={cn('nm-btn nm-btn--primary', collapsed ? 'nm-btn--icon' : 'w-full')}
        >
          <Upload size={16} />
          {!collapsed && <span>사진 올리기</span>}
        </button>
      </div>

      <nav className="flex-1 overflow-y-auto nm-scroll px-2 py-4 space-y-1">
        {navItems.map((item) => {
          const Icon = item.icon;
          if (item.disabled) {
            return (
              <span
                key={item.label}
                title={item.label}
                aria-label={item.label}
                className={cn(
                  'flex items-center rounded-md h-9 text-sm text-text-tertiary cursor-not-allowed',
                  collapsed ? 'justify-center' : 'gap-3 px-3',
                )}
              >
                <Icon size={18} />
                {!collapsed && (
                  <>
                    <span className="flex-1">{item.label}</span>
                    {item.badge && (
                      <span className="text-[11px] font-medium px-1.5 rounded bg-elevated text-text-secondary">
                        {item.badge}
                      </span>
                    )}
                  </>
                )}
              </span>
            );
          }
          return (
            <NavLink
              key={item.to}
              to={item.to}
              title={item.label}
              aria-label={item.label}
              className={({ isActive }) =>
                cn(
                  'flex items-center rounded-md h-9 text-sm transition-colors',
                  collapsed ? 'justify-center' : 'gap-3 px-3',
                  isActive
                    ? 'bg-accent text-accent-bright'
                    : 'text-text-secondary hover:bg-white/[0.04] hover:text-text-primary',
                )
              }
            >
              <Icon size={18} />
              {!collapsed && <span className="flex-1">{item.label}</span>}
            </NavLink>
          );
        })}
      </nav>

      <div className={cn('border-t border-[var(--border-subtle)]', collapsed ? 'p-2 flex justify-center' : 'p-3')}>
        {member ? (
          <NavLink
            to="/member/info"
            title={member.nickname ?? '내 정보'}
            aria-label="내 정보"
            className={cn(
              'flex items-center rounded-md hover:bg-white/[0.04]',
              collapsed ? 'justify-center w-9 h-9' : 'gap-3 px-2 h-12',
            )}
          >
            <div className="w-8 h-8 rounded-full bg-elevated flex items-center justify-center text-sm font-semibold shrink-0">
              {member.nickname?.[0] ?? '?'}
            </div>
            {!collapsed && (
              <>
                <div className="flex-1 min-w-0">
                  <div className="text-sm font-medium truncate">{member.nickname}</div>
                  <div className="text-[11px] text-text-tertiary truncate font-en">
                    {member.email}
                  </div>
                </div>
                <ChevronsUpDown size={14} className="text-text-tertiary" />
              </>
            )}
          </NavLink>
        ) : (
          <NavLink
            to="/member/login"
            title="로그인"
            aria-label="로그인"
            className={cn('nm-btn nm-btn--outline', collapsed ? 'nm-btn--icon' : 'w-full')}
          >
            {collapsed ? <LogIn size={16} /> : '로그인'}
          </NavLink>
        )}
      </div>
    </aside>
  );
}
