import { Navigate, useLocation } from 'react-router-dom';
import type { ReactNode } from 'react';
import { useAuthStore } from '@/stores/authStore';

interface Props {
  children: ReactNode;
}

export function ProtectedRoute({ children }: Props) {
  const isLogin = useAuthStore((s) => s.isLogin);
  const location = useLocation();

  if (!isLogin) {
    return <Navigate to="/member/login" state={{ from: location.pathname }} replace />;
  }

  return <>{children}</>;
}
