import { useEffect } from 'react';
import { useAuthStore } from '@/stores/authStore';
import { tokenCookies } from '@/lib/axios';
import { membersApi } from '@/api/members';
import { toast } from 'sonner';

export function useAuthBootstrap() {
  const setMember = useAuthStore((s) => s.setMember);
  const clearMember = useAuthStore((s) => s.clearMember);

  useEffect(() => {
    if (!tokenCookies.hasAny()) {
      return;
    }
    membersApi
      .getProfile()
      .then((member) => setMember(member))
      .catch(() => {
        clearMember();
      });
  }, [setMember, clearMember]);
}

export function useLogout() {
  const clearMember = useAuthStore((s) => s.clearMember);
  return async () => {
    try {
      await membersApi.logout();
    } catch {
      // 서버 측 실패해도 로컬 정리는 진행
    }
    tokenCookies.clear();
    clearMember();
    toast.info('로그아웃 되었습니다.');
  };
}
