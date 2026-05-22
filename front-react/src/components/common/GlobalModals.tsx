import { AuthPromptModal } from './AuthPromptModal';
import { UploadModal } from '@/components/upload/UploadModal';
import { useUIStore } from '@/stores/uiStore';

export function GlobalModals() {
  const authPromptOpen = useUIStore((s) => s.authPromptOpen);
  const authPromptMessage = useUIStore((s) => s.authPromptMessage);
  const closeAuthPrompt = useUIStore((s) => s.closeAuthPrompt);
  const uploadOpen = useUIStore((s) => s.uploadOpen);
  const closeUpload = useUIStore((s) => s.closeUpload);

  return (
    <>
      <AuthPromptModal
        open={authPromptOpen}
        onClose={closeAuthPrompt}
        message={authPromptMessage ?? undefined}
      />
      <UploadModal open={uploadOpen} onClose={closeUpload} />
    </>
  );
}
