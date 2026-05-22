import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { toast } from 'sonner';
import { useState } from 'react';
import { membersApi } from '@/api/members';
import { authApi, type SocialProvider } from '@/api/auth';
import { useAuthStore } from '@/stores/authStore';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { NmLogo } from '@/components/icons/NmLogoMark';

const schema = z.object({
  email: z.string().email('이메일 형식이 아닙니다.'),
  password: z.string().min(1, '비밀번호를 입력해주세요.'),
});

type FormValues = z.infer<typeof schema>;

interface LocationState {
  from?: string;
}

export function LoginPage() {
  const setMember = useAuthStore((s) => s.setMember);
  const navigate = useNavigate();
  const location = useLocation();
  const [socialLoading, setSocialLoading] = useState<SocialProvider | null>(null);

  const from = (location.state as LocationState | null)?.from ?? '/';

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<FormValues>({ resolver: zodResolver(schema) });

  const onSubmit = handleSubmit(async (values) => {
    try {
      const member = await membersApi.login(values);
      setMember(member);
      toast.success(`환영합니다, ${member.nickname}님`);
      navigate(from, { replace: true });
    } catch {
      // axios 인터셉터가 토스트 처리
    }
  });

  const handleSocial = async (provider: SocialProvider) => {
    setSocialLoading(provider);
    try {
      const { url } = await authApi.getAuthorizeUrl(provider);
      window.location.href = url;
    } catch {
      setSocialLoading(null);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center px-6 py-12 bg-background">
      <div className="w-full max-w-sm">
        <div className="flex justify-center mb-8">
          <Link to="/">
            <NmLogo />
          </Link>
        </div>

        <div className="nm-card p-7">
          <h1 className="text-xl font-semibold tracking-tight mb-1">로그인</h1>
          <p className="text-sm text-text-secondary mb-6">
            오늘의 고양이를 보러 가요.
          </p>

          <form onSubmit={onSubmit} className="space-y-4">
            <div className="space-y-1.5">
              <Label htmlFor="email">이메일</Label>
              <Input
                id="email"
                type="email"
                autoComplete="email"
                placeholder="you@example.com"
                {...register('email')}
              />
              {errors.email && (
                <p className="text-xs text-error">{errors.email.message}</p>
              )}
            </div>

            <div className="space-y-1.5">
              <Label htmlFor="password">비밀번호</Label>
              <Input
                id="password"
                type="password"
                autoComplete="current-password"
                {...register('password')}
              />
              {errors.password && (
                <p className="text-xs text-error">{errors.password.message}</p>
              )}
            </div>

            <Button type="submit" disabled={isSubmitting} className="w-full" size="lg">
              {isSubmitting ? '로그인 중...' : '로그인'}
            </Button>
          </form>

          <div className="my-6 flex items-center gap-3">
            <div className="flex-1 h-px bg-[var(--border-default)]" />
            <span className="text-xs text-text-tertiary">또는</span>
            <div className="flex-1 h-px bg-[var(--border-default)]" />
          </div>

          <div className="space-y-2">
            <button
              type="button"
              onClick={() => handleSocial('kakao')}
              disabled={socialLoading !== null}
              className="nm-btn nm-btn--outline nm-btn--lg w-full"
            >
              {socialLoading === 'kakao' ? '이동 중...' : 'Kakao로 계속하기'}
            </button>
            <button
              type="button"
              onClick={() => handleSocial('google')}
              disabled={socialLoading !== null}
              className="nm-btn nm-btn--outline nm-btn--lg w-full"
            >
              {socialLoading === 'google' ? '이동 중...' : 'Google로 계속하기'}
            </button>
          </div>

          <p className="text-center text-sm text-text-secondary mt-6">
            아직 계정이 없으세요?{' '}
            <Link to="/member/join" className="text-accent-bright hover:underline">
              회원가입
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
