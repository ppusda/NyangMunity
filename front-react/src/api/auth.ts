import { axiosClient } from '@/lib/axios';
import type { MemberResponse } from '@/types';

export type SocialProvider = 'kakao' | 'google';

export const authApi = {
  getAuthorizeUrl: (provider: SocialProvider) =>
    axiosClient.get<{ url: string }>(`/auth/${provider}/url`).then((r) => r.data),

  exchangeCode: (provider: SocialProvider, code: string) =>
    axiosClient
      .get<MemberResponse>(`/auth/${provider}/callback`, { params: { code } })
      .then((r) => r.data),
};
