import { axiosClient } from '@/lib/axios';
import type { MemberResponse } from '@/types';

export interface LoginPayload {
  email: string;
  password: string;
}

export interface JoinPayload {
  email: string;
  password: string;
  nickname: string;
  birthday: string;
}

export interface ProfileUpdatePayload {
  nickname?: string;
  currentPassword?: string;
  newPassword?: string;
}

export const membersApi = {
  login: (payload: LoginPayload) =>
    axiosClient.post<MemberResponse>('/members/login', payload).then((r) => r.data),

  logout: () => axiosClient.post<void>('/members/logout').then((r) => r.data),

  join: (payload: JoinPayload) =>
    axiosClient.post<void>('/members/join', payload).then((r) => r.data),

  getProfile: () =>
    axiosClient.get<MemberResponse>('/members/profile').then((r) => r.data),

  updateProfile: (payload: ProfileUpdatePayload) =>
    axiosClient.patch<MemberResponse>('/members/profile', payload).then((r) => r.data),

  cancel: () => axiosClient.post<void>('/members/cancel').then((r) => r.data),
};
