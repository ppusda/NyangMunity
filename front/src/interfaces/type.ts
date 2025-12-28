export interface Post {
    id: string;
    content: string;
    postImages: Array<PostImage>;
    createDate: string;
    memberId: number;
    writer: string;
}

export interface PostLike {
    id: string | null;
    imageInfo: PostImage | null;
    nickname: string | null;
    message: string;
}

export interface PostImage {
    id: string | null;
    url: string;
    likeState: boolean;
}

export interface Image {
    id: string | null;
    filename: string | null;
    url: string;
    source: "gallery" | "upload";
}

export interface Member {
    id: string;
    email: string;
    password: string;
    nickname: string;
}

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

export interface ImageLikeResponse {
    imageId: String;
    state: boolean;
}

// CategoryChild 타입 (하위 카테고리용)
interface CategoryChild {
    id: string;
    name: string;
    type: 'post' | 'image';
    provider?: 'NYANGMUNITY' | 'TENOR';  // 선택적 속성
}

// Category 타입 (최상위 카테고리용)
interface Category {
    id: string;
    name: string;
    icon?: string;
    type: 'post' | 'image' | 'group';
    provider?: 'NYANGMUNITY' | 'TENOR';
    expanded?: boolean;
    children?: CategoryChild[];
}