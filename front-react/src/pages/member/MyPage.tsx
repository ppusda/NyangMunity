import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { toast } from 'sonner';
import { useQuery } from '@tanstack/react-query';
import { membersApi } from '@/api/members';
import { useAuthStore } from '@/stores/authStore';
import { useLogout } from '@/hooks/useAuth';
import { tokenCookies } from '@/lib/axios';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

const schema = z
  .object({
    nickname: z.string().min(1, '닉네임을 입력해주세요.').max(30, '30자 이하로 입력해주세요.'),
    currentPassword: z.string().optional(),
    newPassword: z.string().optional(),
  })
  .refine(
    (v) => !v.newPassword || (v.currentPassword && v.currentPassword.length > 0),
    { message: '현재 비밀번호를 입력해주세요.', path: ['currentPassword'] },
  );

type FormValues = z.infer<typeof schema>;

export function MyPage() {
  const member = useAuthStore((s) => s.member);
  const setMember = useAuthStore((s) => s.setMember);
  const logout = useLogout();
  const navigate = useNavigate();
  const [confirmingCancel, setConfirmingCancel] = useState(false);

  const { data: profile } = useQuery({
    queryKey: ['members', 'profile'],
    queryFn: () => membersApi.getProfile(),
    initialData: member ?? undefined,
  });

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { nickname: profile?.nickname ?? '' },
  });

  useEffect(() => {
    if (profile) reset({ nickname: profile.nickname });
  }, [profile, reset]);

  const onSubmit = handleSubmit(async (values) => {
    try {
      const updated = await membersApi.updateProfile({
        nickname: values.nickname,
        currentPassword: values.currentPassword || undefined,
        newPassword: values.newPassword || undefined,
      });
      setMember(updated);
      toast.success('프로필이 업데이트됐어요.');
      reset({ nickname: updated.nickname, currentPassword: '', newPassword: '' });
    } catch {
      // axios 인터셉터가 토스트 처리
    }
  });

  const handleCancel = async () => {
    try {
      await membersApi.cancel();
      tokenCookies.clear();
      useAuthStore.getState().clearMember();
      toast.info('계정이 삭제되었어요.');
      navigate('/', { replace: true });
    } catch {
      // 토스트는 인터셉터
    }
  };

  return (
    <div className="px-8 py-10 max-w-2xl">
      <h1 className="text-2xl font-semibold tracking-tight">마이페이지</h1>
      <p className="text-sm text-text-secondary mt-1">계정 정보를 관리하세요.</p>

      <div className="nm-card p-7 mt-8 space-y-6">
        <div className="flex items-center gap-4">
          <div className="w-16 h-16 rounded-full bg-elevated flex items-center justify-center text-xl font-semibold">
            {profile?.nickname?.[0] ?? '?'}
          </div>
          <div>
            <div className="text-sm text-text-tertiary">이메일</div>
            <div className="text-base">{profile?.email ?? '—'}</div>
          </div>
        </div>

        <form onSubmit={onSubmit} className="space-y-5">
          <div className="space-y-1.5">
            <Label htmlFor="nickname">닉네임</Label>
            <Input id="nickname" {...register('nickname')} />
            {errors.nickname && (
              <p className="text-xs text-error">{errors.nickname.message}</p>
            )}
          </div>

          <div className="border-t border-[var(--border-subtle)] pt-5 space-y-1.5">
            <Label htmlFor="currentPassword">현재 비밀번호 (변경 시에만)</Label>
            <Input
              id="currentPassword"
              type="password"
              autoComplete="current-password"
              {...register('currentPassword')}
            />
            {errors.currentPassword && (
              <p className="text-xs text-error">{errors.currentPassword.message}</p>
            )}
          </div>

          <div className="space-y-1.5">
            <Label htmlFor="newPassword">새 비밀번호</Label>
            <Input
              id="newPassword"
              type="password"
              autoComplete="new-password"
              {...register('newPassword')}
            />
          </div>

          <div className="flex gap-2">
            <Button type="submit" disabled={isSubmitting}>
              {isSubmitting ? '저장 중...' : '저장'}
            </Button>
            <Button type="button" variant="outline" onClick={logout}>
              로그아웃
            </Button>
          </div>
        </form>
      </div>

      <div className="mt-8 border-t border-[var(--border-subtle)] pt-6">
        {confirmingCancel ? (
          <div className="nm-card p-5 border-error/40">
            <p className="text-sm text-text-primary mb-3">
              정말 탈퇴하시겠어요? 이 작업은 되돌릴 수 없어요.
            </p>
            <div className="flex gap-2">
              <Button variant="destructive" onClick={handleCancel}>
                네, 탈퇴할게요
              </Button>
              <Button variant="outline" onClick={() => setConfirmingCancel(false)}>
                취소
              </Button>
            </div>
          </div>
        ) : (
          <Button
            variant="ghost"
            onClick={() => setConfirmingCancel(true)}
            className="text-error hover:text-error"
          >
            회원 탈퇴
          </Button>
        )}
      </div>
    </div>
  );
}
