import axios, { AxiosError, type AxiosRequestConfig, type InternalAxiosRequestConfig } from 'axios';
import Cookies from 'js-cookie';
import { toast } from 'sonner';
import { useAuthStore } from '@/stores/authStore';
import type { MemberAuthenticationResponse } from '@/types';

type RetryableRequest = InternalAxiosRequestConfig & { _retry?: boolean };

const ACCESS_COOKIE = 'accessToken';

export const axiosClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  withCredentials: true,
  headers: { 'Content-Type': 'application/json' },
});

axiosClient.interceptors.request.use((config) => {
  const token = Cookies.get(ACCESS_COOKIE);
  if (token) {
    config.headers.set('Authorization', `Bearer ${token}`);
  }
  return config;
});

let isRefreshing = false;
let refreshSubscribers: Array<(token?: string) => void> = [];

function addRefreshSubscriber(cb: (token?: string) => void) {
  refreshSubscribers.push(cb);
}

function onRefreshed(token?: string) {
  refreshSubscribers.forEach((cb) => cb(token));
  refreshSubscribers = [];
}

// Refresh 쿠키는 HttpOnly 라 JS 가 직접 첨부할 수 없지만 withCredentials:true 로 자동 전송된다.
// 백엔드가 응답 Set-Cookie 로 access·refresh 를 함께 갱신해 내려주므로 프론트는 별도 저장이 필요 없다.
async function requestReissue(): Promise<MemberAuthenticationResponse> {
  const response = await axios.post<MemberAuthenticationResponse>(
    `${import.meta.env.VITE_API_BASE_URL}/tokens`,
    null,
    { withCredentials: true },
  );
  return response.data;
}

// Access 만 JS-readable 이라 클라이언트가 직접 지울 수 있다. Refresh 는 HttpOnly 이므로
// 백엔드 logout API 의 응답 Set-Cookie 가 maxAge=0 으로 제거하는 것을 신뢰한다.
function clearLocalAuth() {
  Cookies.remove(ACCESS_COOKIE);
  useAuthStore.getState().clearMember();
}

axiosClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError<{ code?: string; message?: string }>) => {
    const originalRequest = error.config as RetryableRequest | undefined;

    if (error.response?.status !== 401) {
      const message = error.response?.data?.message ?? '요청 중 오류가 발생했습니다.';
      if (!error.config?.url?.includes('/members/profile')) {
        toast.warning(message);
      }
      return Promise.reject(error);
    }

    if (!originalRequest) return Promise.reject(error);

    // TOKEN_EXPIRED 만 재발급 신호. TOKEN_INVALID·그 외 401 (블랙리스트·서명 위조·세션 만료) 은
    // 재발급해도 같은 코드로 또 거부될 가능성이 높아 즉시 로컬 인증을 정리한다.
    if (error.response.data?.code !== 'TOKEN_EXPIRED') {
      clearLocalAuth();
      return Promise.reject(error);
    }

    if (originalRequest._retry) {
      toast.warning('재로그인 해주세요!');
      clearLocalAuth();
      return Promise.reject(error);
    }

    if (!isRefreshing) {
      isRefreshing = true;
      originalRequest._retry = true;

      try {
        const reissued = await requestReissue();
        useAuthStore.getState().setMember(reissued.memberInfoResponse);

        const newToken = reissued.memberTokens.accessToken;
        originalRequest.headers.set('Authorization', `Bearer ${newToken}`);
        onRefreshed(newToken);

        return await axiosClient(originalRequest as AxiosRequestConfig);
      } catch (reissueError) {
        toast.warning('세션이 만료되었습니다. 다시 로그인해주세요.');
        onRefreshed();
        clearLocalAuth();
        return Promise.reject(reissueError);
      } finally {
        isRefreshing = false;
      }
    }

    return new Promise((resolve, reject) => {
      addRefreshSubscriber((token) => {
        if (token) {
          originalRequest.headers.set('Authorization', `Bearer ${token}`);
          resolve(axiosClient(originalRequest as AxiosRequestConfig));
        } else {
          reject(error);
        }
      });
    });
  },
);

export const tokenCookies = {
  clear: clearLocalAuth,
  // Refresh 쿠키는 HttpOnly 라 JS 가 못 읽으므로 persisted 인증 상태로 추정한다.
  hasAny: () => useAuthStore.getState().isLogin || Boolean(Cookies.get(ACCESS_COOKIE)),
};
