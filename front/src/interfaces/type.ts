export interface Post {
    id: string;
    content: string;
    postImages: Array<PostImage>;
    createDate: string;
    uid: number;
    writer: string;
}

export interface PostLike {
    id: string|null;
    postImages: Array<PostImage>|null;
    nickname: string|null;
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
    birthday: string;
}

export interface MemberResponse {
    id: string;
    email: string;
    nickname: string;
};
