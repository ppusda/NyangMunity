import { useMemo } from 'react';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { imagesApi } from '@/api/images';
import type { ImageDetailResponse, ImagePage } from '@/types';

export function useImageDetail(imageId: string | undefined) {
  return useQuery<ImageDetailResponse>({
    queryKey: ['images', 'detail', imageId],
    enabled: Boolean(imageId),
    queryFn: () => imagesApi.getById(imageId!),
    staleTime: 30_000,
  });
}

// 갤러리 리스트 캐시를 그대로 활용해서 현재 위치/이웃 ID를 계산한다.
export function useGalleryNeighbors(imageId: string | undefined) {
  const qc = useQueryClient();

  return useMemo(() => {
    const empty = {
      prev: null as string | null,
      next: null as string | null,
      position: null as { index: number; total: number } | null,
    };
    if (!imageId) return empty;

    const listCaches = qc.getQueriesData<{ pages: ImagePage[] }>({
      queryKey: ['images', 'list'],
      exact: false,
    });
    for (const [, data] of listCaches) {
      if (!data) continue;
      const flat = data.pages.flatMap((p) => p.content);
      const idx = flat.findIndex((img) => img.id === imageId);
      if (idx >= 0) {
        return {
          prev: idx > 0 ? flat[idx - 1].id : null,
          next: idx < flat.length - 1 ? flat[idx + 1].id : null,
          position: { index: idx + 1, total: flat.length },
        };
      }
    }
    return empty;
  }, [imageId, qc]);
}
