import { useEffect, useRef } from 'react';
import { useNavigate, useParams, useSearchParams } from 'react-router-dom';
import { toast } from 'sonner';
import { authApi, type SocialProvider } from '@/api/auth';
import { useAuthStore } from '@/stores/authStore';

export function SocialLoginCallbackPage() {
  const { provider } = useParams<{ provider: string }>();
  const [params] = useSearchParams();
  const navigate = useNavigate();
  const setMember = useAuthStore((s) => s.setMember);
  const consumed = useRef(false);

  useEffect(() => {
    if (consumed.current) return;
    consumed.current = true;

    const code = params.get('code');
    if (!provider || !code || (provider !== 'kakao' && provider !== 'google')) {
      toast.error('잘못된 소셜 로그인 요청입니다.');
      navigate('/member/login', { replace: true });
      return;
    }

    authApi
      .exchangeCode(provider as SocialProvider, code)
      .then((member) => {
        setMember(member);
        toast.success(`환영합니다, ${member.nickname}님`);
        navigate('/', { replace: true });
      })
      .catch(() => {
        navigate('/member/login', { replace: true });
      });
  }, [provider, params, navigate, setMember]);

  return (
    <div className="min-h-screen flex items-center justify-center text-text-secondary">
      로그인 처리 중...
    </div>
  );
}
