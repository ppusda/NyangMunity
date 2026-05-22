import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Link, useNavigate } from 'react-router-dom';
import { toast } from 'sonner';
import { membersApi } from '@/api/members';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { NmLogo } from '@/components/icons/NmLogoMark';

const schema = z
  .object({
    email: z.string().email('이메일 형식이 아닙니다.'),
    password: z.string().min(8, '비밀번호는 8자 이상이어야 합니다.'),
    passwordConfirm: z.string(),
    nickname: z.string().min(1, '닉네임을 입력해주세요.').max(30),
    birthday: z.string().min(1, '생년월일을 입력해주세요.'),
  })
  .refine((v) => v.password === v.passwordConfirm, {
    message: '비밀번호가 일치하지 않습니다.',
    path: ['passwordConfirm'],
  });

type FormValues = z.infer<typeof schema>;

export function JoinPage() {
  const navigate = useNavigate();
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<FormValues>({ resolver: zodResolver(schema) });

  const onSubmit = handleSubmit(async (values) => {
    try {
      await membersApi.join({
        email: values.email,
        password: values.password,
        nickname: values.nickname,
        birthday: values.birthday,
      });
      toast.success('가입이 완료됐어요. 로그인해주세요.');
      navigate('/member/login', { replace: true });
    } catch {
      // 인터셉터가 토스트
    }
  });

  return (
    <div className="min-h-screen flex items-center justify-center px-6 py-12 bg-background">
      <div className="w-full max-w-sm">
        <div className="flex justify-center mb-8">
          <Link to="/">
            <NmLogo />
          </Link>
        </div>

        <div className="nm-card p-7">
          <h1 className="text-xl font-semibold tracking-tight mb-1">회원가입</h1>
          <p className="text-sm text-text-secondary mb-6">계정을 만들어 오늘부터 시작하세요.</p>

          <form onSubmit={onSubmit} className="space-y-4">
            <div className="space-y-1.5">
              <Label htmlFor="j-email">이메일</Label>
              <Input id="j-email" type="email" {...register('email')} />
              {errors.email && <p className="text-xs text-error">{errors.email.message}</p>}
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="j-password">비밀번호</Label>
              <Input id="j-password" type="password" {...register('password')} />
              {errors.password && (
                <p className="text-xs text-error">{errors.password.message}</p>
              )}
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="j-passwordConfirm">비밀번호 확인</Label>
              <Input id="j-passwordConfirm" type="password" {...register('passwordConfirm')} />
              {errors.passwordConfirm && (
                <p className="text-xs text-error">{errors.passwordConfirm.message}</p>
              )}
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="j-nickname">닉네임</Label>
              <Input id="j-nickname" {...register('nickname')} />
              {errors.nickname && (
                <p className="text-xs text-error">{errors.nickname.message}</p>
              )}
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="j-birthday">생년월일</Label>
              <Input id="j-birthday" type="date" {...register('birthday')} />
              {errors.birthday && (
                <p className="text-xs text-error">{errors.birthday.message}</p>
              )}
            </div>
            <Button type="submit" disabled={isSubmitting} className="w-full" size="lg">
              {isSubmitting ? '가입 중...' : '가입하기'}
            </Button>
          </form>

          <p className="text-center text-sm text-text-secondary mt-6">
            이미 계정이 있으세요?{' '}
            <Link to="/member/login" className="text-accent-bright hover:underline">
              로그인
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
