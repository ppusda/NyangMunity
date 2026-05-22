import { create } from 'zustand';
import type { MemberResponse } from '@/types';

interface AuthState {
  member: MemberResponse | null;
  isLogin: boolean;
  setMember: (member: MemberResponse) => void;
  clearMember: () => void;
}

const STORAGE_KEY = 'nyang.member';

function readPersisted(): MemberResponse | null {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? (JSON.parse(raw) as MemberResponse) : null;
  } catch {
    return null;
  }
}

const persisted = readPersisted();

export const useAuthStore = create<AuthState>((set) => ({
  member: persisted,
  isLogin: persisted !== null,
  setMember: (member) => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(member));
    set({ member, isLogin: true });
  },
  clearMember: () => {
    localStorage.removeItem(STORAGE_KEY);
    set({ member: null, isLogin: false });
  },
}));
