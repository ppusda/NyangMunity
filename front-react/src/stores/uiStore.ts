import { create } from 'zustand';

interface UIState {
  authPromptOpen: boolean;
  authPromptMessage: string | null;
  uploadOpen: boolean;
  openAuthPrompt: (message?: string) => void;
  closeAuthPrompt: () => void;
  openUpload: () => void;
  closeUpload: () => void;
}

export const useUIStore = create<UIState>((set) => ({
  authPromptOpen: false,
  authPromptMessage: null,
  uploadOpen: false,
  openAuthPrompt: (message) =>
    set({ authPromptOpen: true, authPromptMessage: message ?? null }),
  closeAuthPrompt: () => set({ authPromptOpen: false, authPromptMessage: null }),
  openUpload: () => set({ uploadOpen: true }),
  closeUpload: () => set({ uploadOpen: false }),
}));
