import { useInfiniteQuery, useQuery } from '@tanstack/react-query';
import { imagesApi } from '@/api/images';
import type { ImagePage, Provider, SortKey } from '@/types';

const PAGE_SIZE = 24;

interface UseGalleryImagesParams {
  sort: SortKey;
  provider?: Provider;
}

export function useGalleryImages({ sort, provider }: UseGalleryImagesParams) {
  return useInfiniteQuery<ImagePage>({
    queryKey: ['images', 'list', { sort, provider }],
    queryFn: ({ pageParam = 0 }) =>
      imagesApi.list({ page: pageParam as number, size: PAGE_SIZE, sort, provider }),
    initialPageParam: 0,
    getNextPageParam: (last) => {
      if (last.last) return undefined;
      const total = last.totalPages;
      const next = last.number + 1;
      if (typeof total === 'number' && next >= total) return undefined;
      return next;
    },
  });
}

export function useTopLikedImage() {
  return useQuery({
    queryKey: ['images', 'topLike'],
    queryFn: () => imagesApi.topLike(),
    staleTime: 60_000,
  });
}

export function usePopularImages(size = 6) {
  return useQuery({
    queryKey: ['images', 'popular', size],
    queryFn: () => imagesApi.popular(size),
    staleTime: 60_000,
  });
}

export function useProviders() {
  return useQuery({
    queryKey: ['images', 'providers'],
    queryFn: () => imagesApi.providers(),
    staleTime: 5 * 60_000,
  });
}
