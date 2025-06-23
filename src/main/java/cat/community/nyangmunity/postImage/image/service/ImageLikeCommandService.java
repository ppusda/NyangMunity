package cat.community.nyangmunity.postImage.image.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cat.community.nyangmunity.member.entity.Member;
import cat.community.nyangmunity.postImage.image.entity.Image;
import cat.community.nyangmunity.postImage.image.entity.ImageLike;
import cat.community.nyangmunity.postImage.image.repository.ImageLikeRepository;
import cat.community.nyangmunity.postImage.image.response.ImageLikeResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageLikeCommandService {

	private final ImageLikeQueryService imageQueryService;
	private final ImageQueryService imageService;

	private final ImageLikeRepository imageLikeRepository;

	@Transactional
	public ImageLikeResponse likeImageProcess(String imageId, Member member) {
		Optional<ImageLike> imageLike = imageQueryService.findPostImageLike(imageId, member.getId());

		// 만약, 이미지 좋아요를 누른 상태라면 취소 작업
		if (imageLike.isPresent()) {
			unlikeImage(imageLike.get());
			return ImageLikeResponse.builder()
				.imageId(imageId)
				.state(false)
				.build();
		}

		// 좋아요를 누른 상태가 아니라면 좋아요 등록
		Image image = imageService.findImageById(imageId);
		likeImage(ImageLike.builder()
			.member(member)
			.image(image)
			.build()
		);

		return ImageLikeResponse.builder()
			.imageId(imageId)
			.state(false)
			.build();
	}

	@Transactional
	public void likeImage(ImageLike imageLike) {
		imageLikeRepository.save(imageLike);
	}

	@Transactional
	public void unlikeImage(ImageLike imageLike) {
		imageLikeRepository.delete(imageLike);
	}
}
