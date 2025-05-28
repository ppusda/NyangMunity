package cat.community.nyangmunity.postImage.repository;

import cat.community.nyangmunity.postImage.entity.PostImage;

public interface PostImageLikeRepositoryCustom {

	PostImage getMaxLikePostImage();
}
