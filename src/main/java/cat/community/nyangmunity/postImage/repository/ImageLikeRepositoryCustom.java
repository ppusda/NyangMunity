package cat.community.nyangmunity.postImage.repository;

import cat.community.nyangmunity.postImage.image.entity.Image;

public interface ImageLikeRepositoryCustom {

	Image getMaxLikeImage();
}
