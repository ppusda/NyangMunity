package cat.community.nyangmunity.post.repository;

import cat.community.nyangmunity.post.entity.Post;

public interface PostLikeRepositoryCustom {

    Post getMaxLikePost();
}
