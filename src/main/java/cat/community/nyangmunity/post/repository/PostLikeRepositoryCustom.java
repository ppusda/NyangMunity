package cat.community.nyangmunity.post.repository;

import cat.community.nyangmunity.post.entity.PostImage;

public interface PostLikeRepositoryCustom {

	PostImage getMaxLikePostImage();
}
