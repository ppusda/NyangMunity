import { Routes, Route } from 'react-router-dom';
import { AppShell } from '@/components/layout/AppShell';
import { ProtectedRoute } from './ProtectedRoute';
import { MainPage } from '@/pages/Main';
import { GalleryPage } from '@/pages/Gallery';
import { LoginPage } from '@/pages/member/Login';
import { JoinPage } from '@/pages/member/Join';
import { MyPage } from '@/pages/member/MyPage';
import { SocialLoginCallbackPage } from '@/pages/member/SocialLoginCallback';

export function AppRouter() {
  return (
    <Routes>
      <Route element={<AppShell />}>
        <Route path="/" element={<MainPage />} />
        <Route path="/posts" element={<GalleryPage />} />
        <Route
          path="/member/info"
          element={
            <ProtectedRoute>
              <MyPage />
            </ProtectedRoute>
          }
        />
      </Route>
      <Route path="/member/login" element={<LoginPage />} />
      <Route path="/member/join" element={<JoinPage />} />
      <Route path="/auth/:provider/callback" element={<SocialLoginCallbackPage />} />
    </Routes>
  );
}
