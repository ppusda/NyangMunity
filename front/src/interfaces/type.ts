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
    postImages: Array<PostImage> | null;
    nickname: string | null;
    message: string;
}

export interface PostImage {
    id: string | null;
    url: string;
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