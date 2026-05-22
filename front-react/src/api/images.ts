import axios from 'axios';
import { axiosClient } from '@/lib/axios';
import type {
  ImageDetailResponse,
  ImageItem,
  ImageLikeResponse,
  ImagePage,
  ImageUploadCompleteRequest,
  Provider,
  SortKey,
  TopLikeResponse,
  UploadUrlResponse,
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

  getById: (imageId: string): Promise<ImageDetailResponse> =>
    axiosClient.get<ImageDetailResponse>(`/images/${imageId}`).then((r) => r.data),

  requestUploadUrl: (filename: string): Promise<UploadUrlResponse> =>
    axiosClient
      .get<UploadUrlResponse>('/images/upload', { params: { filename } })
      .then((r) => r.data),

  completeUpload: (payload: ImageUploadCompleteRequest): Promise<ImageDetailResponse> =>
    axiosClient.post<ImageDetailResponse>('/images', payload).then((r) => r.data),

  // S3 presigned URL 에 PUT 으로 직접 업로드한다. 인증 헤더가 안 가도록 별도 axios 인스턴스 사용.
  uploadToS3: (uploadUrl: string, file: File): Promise<void> =>
    axios
      .put(uploadUrl, file, {
        headers: { 'Content-Type': 'application/octet-stream' },
      })
      .then(() => undefined),
};
