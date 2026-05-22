import { axiosClient } from '@/lib/axios';
import type {
  ImageItem,
  ImageLikeResponse,
  ImagePage,
  Provider,
  SortKey,
  TopLikeResponse,
} from '@/types';

export interface ListImagesParams {
  page?: number;
  size?: number;
  sort?: SortKey;
  provider?: Provider;
}

const providerLabelToCode: Record<string, Provider> = {
  Nyangmunity: 'NYANGMUNITY',
  Tenor: 'TENOR',
  TheCatAPI: 'THECATAPI',
};

const sortKeyToBackend: Record<SortKey, string> = {
  latest: 'latest',
  likes: 'popular',
  views: 'views',
};

export const imagesApi = {
  list: (params: ListImagesParams = {}) =>
    axiosClient
      .get<ImagePage>('/images', {
        params: {
          page: params.page ?? 0,
          size: params.size ?? 24,
          sort: sortKeyToBackend[params.sort ?? 'latest'],
          provider: params.provider,
        },
      })
      .then((r) => r.data),

  topLike: () =>
    axiosClient.get<TopLikeResponse>('/images/likes').then((r) => r.data),

  toggleLike: (imageId: string) =>
    axiosClient.post<ImageLikeResponse>('/images/likes', { imageId }).then((r) => r.data),

  providers: () =>
    axiosClient
      .get<{ Provider: string[] }>('/images/providers')
      .then((r) => {
        const labels = r.data?.Provider ?? [];
        return labels
          .map<Provider | null>((label) => providerLabelToCode[label] ?? null)
          .filter((p): p is Provider => p !== null);
      }),

  popular: (size = 6): Promise<ImageItem[]> =>
    axiosClient
      .get<ImagePage>('/images', { params: { page: 0, size, sort: 'popular' } })
      .then((r) => r.data.content),
};
