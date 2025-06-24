package cat.community.nyangmunity.postImage.image.repository;

import java.util.List;

import cat.community.nyangmunity.member.entity.Member;
import cat.community.nyangmunity.postImage.image.entity.Image;

public interface ImageLikeRepositoryCustom {

	Image getMaxLikeImage();

	List<String> fetchLikedImageIds(List<String> imageIds, Member member);
}
