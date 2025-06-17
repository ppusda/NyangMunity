package cat.community.nyangmunity.postImage.image.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cat.community.nyangmunity.global.exception.post.PostNotFoundException;
import cat.community.nyangmunity.postImage.image.entity.Image;
import cat.community.nyangmunity.postImage.image.entity.ImageLike;
import cat.community.nyangmunity.postImage.image.repository.ImageLikeRepository;
import cat.community.nyangmunity.postImage.image.response.MaxLikePostImageResponse;
import cat.community.nyangmunity.postImage.response.PostImageResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageLikeQueryService {

	private final ImageLikeRepository imageLikeRepository;

	@Transactional(readOnly = true)
	public MaxLikePostImageResponse getMaxLikeImage() {
		try {
			Image image = imageLikeRepository.getMaxLikeImage();
			return MaxLikePostImageResponse.builder()
				.id(image.getId())
				.imageInfo(PostImageResponse.from(image))
				.nickname(image.getMember().getNickname())
				.message("가장 인기 많은 " + image.getMember().getNickname() + "님의 이미지 입니다!")
				.build();
		} catch (PostNotFoundException e) {
			return MaxLikePostImageResponse.builder()
				.message(e.getMessage())
				.build();
		}
	}

	@Transactional(readOnly = true)
	public Optional<ImageLike> findPostImageLike(String imageId, Long memberId) {
		return imageLikeRepository.findByImageIdAndMemberId(imageId, memberId);
	}
}
