import { useMutation, useQueryClient } from '@tanstack/react-query';
import { imagesApi } from '@/api/images';
import { useAuthStore } from '@/stores/authStore';
import { useUIStore } from '@/stores/uiStore';
import type {
  ImageDetailResponse,
  ImageItem,
  ImageLikeResponse,
  ImagePage,
} from '@/types';

function buildSetter(imageId: string, nextLiked: boolean | null) {
  return (img: ImageItem): ImageItem => {
    if (img.id !== imageId) return img;
    const wasLiked = img.likeState ?? false;
    const target = nextLiked ?? !wasLiked;
    if (wasLiked === target) return img;
    return {
      ...img,
      likeState: target,
      likesCount: img.likesCount + (target ? 1 : -1),
    };
  };
}

function applyToCaches(
  qc: ReturnType<typeof useQueryClient>,
  imageId: string,
  nextLiked: boolean | null,
) {
  const setter = buildSetter(imageId, nextLiked);

  qc.getQueriesData<{ pages: ImagePage[] }>({
    queryKey: ['images', 'list'],
    exact: false,
  }).forEach(([key, data]) => {
    if (!data) return;
    qc.setQueryData(key, {
      ...data,
      pages: data.pages.map((p) => ({ ...p, content: p.content.map(setter) })),
    });
  });

  qc.getQueriesData<ImageItem[]>({
    queryKey: ['images', 'popular'],
    exact: false,
  }).forEach(([key, data]) => {
    if (!data) return;
    qc.setQueryData(key, data.map(setter));
  });

  const topLike = qc.getQueryData<{ imageInfo: ImageItem | null }>(['images', 'topLike']);
  if (topLike?.imageInfo) {
    qc.setQueryData(['images', 'topLike'], {
      ...topLike,
      imageInfo: setter(topLike.imageInfo),
    });
  }

  qc.getQueriesData<ImageDetailResponse>({
    queryKey: ['images', 'detail'],
    exact: false,
  }).forEach(([key, data]) => {
    if (!data || data.id !== imageId) return;
    const wasLiked = data.likeState;
    const target = nextLiked ?? !wasLiked;
    if (wasLiked === target) return;
    qc.setQueryData(key, {
      ...data,
      likeState: target,
      likesCount: data.likesCount + (target ? 1 : -1),
    });
  });
}

export function useLikeToggle() {
  const qc = useQueryClient();
  const isLogin = useAuthStore((s) => s.isLogin);
  const openAuthPrompt = useUIStore((s) => s.openAuthPrompt);

  return useMutation<ImageLikeResponse, Error, string>({
    mutationFn: (imageId) => {
      if (!isLogin) {
        openAuthPrompt('좋아요는 로그인 후 이용할 수 있어요.');
        return Promise.reject(new Error('UNAUTHENTICATED'));
      }
      return imagesApi.toggleLike(imageId);
    },
    onMutate: (imageId) => {
      applyToCaches(qc, imageId, null);
    },
    onSuccess: (data, imageId) => {
      // 백엔드가 알려준 최종 상태로 확정. 옵티미스틱과 동일하면 noop.
      applyToCaches(qc, imageId, data.state);
    },
    onError: (_err, imageId) => {
      // 옵티미스틱 롤백 (다시 toggle해서 원복)
      applyToCaches(qc, imageId, null);
    },
  });
}
