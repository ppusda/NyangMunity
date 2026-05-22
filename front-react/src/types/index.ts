export type Provider = 'NYANGMUNITY' | 'TENOR' | 'THECATAPI';

export interface MemberResponse {
  id: string;
  email: string;
  nickname: string;
}

export interface MemberTokens {
  accessToken: string;
  refreshToken: string;
}

export interface MemberAuthenticationResponse {
  memberInfoResponse: MemberResponse;
  memberTokens: MemberTokens;
}

export interface ImageItem {
  id: string;
  name: string | null;
  url: string;
  thumbnailUrl: string | null;
  description: string | null;
  provider: Provider;
  uploadDate: string;
  likesCount: number;
  viewsCount: number;
  expiresAt?: string | null;
  uploader?: string | null;
  tags?: string[];
  likeState?: boolean;
}

export interface ImagePage {
  content: ImageItem[];
  number: number;
  totalElements?: number;
  totalPages?: number;
  last?: boolean;
  first?: boolean;
  numberOfElements?: number;
  empty?: boolean;
}

export interface ImageLikeResponse {
  imageId: string;
  state: boolean;
}

export interface TopLikeResponse {
  id: string | null;
  imageInfo: ImageItem | null;
  nickname: string | null;
  message: string | null;
}

export type SortKey = 'latest' | 'likes' | 'views';
