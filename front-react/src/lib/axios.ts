import axios, { AxiosError, type AxiosRequestConfig, type InternalAxiosRequestConfig } from 'axios';
import Cookies from 'js-cookie';
import { toast } from 'sonner';
import { useAuthStore } from '@/stores/authStore';
import type { MemberAuthenticationResponse } from '@/types';

type RetryableRequest = InternalAxiosRequestConfig & { _retry?: boolean };

const ACCESS_COOKIE = 'accessToken';
const REFRESH_COOKIE = 'refreshToken';

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

async function requestReissue(): Promise<MemberAuthenticationResponse> {
  const response = await axios.post<MemberAuthenticationResponse>(
    `${import.meta.env.VITE_API_BASE_URL}/tokens`,
    null,
    { withCredentials: true },
  );
  return response.data;
}

function saveTokens(tokens: { accessToken: string; refreshToken: string }) {
  Cookies.set(ACCESS_COOKIE, tokens.accessToken);
  Cookies.set(REFRESH_COOKIE, tokens.refreshToken);
}

function clearTokensAndLogout() {
  Cookies.remove(ACCESS_COOKIE);
  Cookies.remove(REFRESH_COOKIE);
  useAuthStore.getState().clearMember();
}

axiosClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError<{ message?: string }>) => {
    const originalRequest = error.config as RetryableRequest | undefined;

    if (error.response?.status !== 401) {
      const message = error.response?.data?.message ?? '요청 중 오류가 발생했습니다.';
      if (!error.config?.url?.includes('/members/profile')) {
        toast.warning(message);
      }
      return Promise.reject(error);
    }

    if (!originalRequest) return Promise.reject(error);

    if (originalRequest._retry) {
      toast.warning('재로그인 해주세요!');
      clearTokensAndLogout();
      return Promise.reject(error);
    }

    if (!isRefreshing) {
      isRefreshing = true;
      originalRequest._retry = true;

      try {
        const reissued = await requestReissue();
        saveTokens(reissued.memberTokens);
        useAuthStore.getState().setMember(reissued.memberInfoResponse);

        const newToken = reissued.memberTokens.accessToken;
        originalRequest.headers.set('Authorization', `Bearer ${newToken}`);
        onRefreshed(newToken);

        return await axiosClient(originalRequest as AxiosRequestConfig);
      } catch (reissueError) {
        toast.warning('세션이 만료되었습니다. 다시 로그인해주세요.');
        onRefreshed();
        clearTokensAndLogout();
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
  save: saveTokens,
  clear: () => {
    Cookies.remove(ACCESS_COOKIE);
    Cookies.remove(REFRESH_COOKIE);
  },
  hasAny: () => Boolean(Cookies.get(ACCESS_COOKIE) || Cookies.get(REFRESH_COOKIE)),
};
