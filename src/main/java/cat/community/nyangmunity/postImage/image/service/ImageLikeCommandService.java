package cat.community.nyangmunity.postImage.image.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cat.community.nyangmunity.global.exception.global.BadRequestException;
import cat.community.nyangmunity.member.entity.Member;
import cat.community.nyangmunity.postImage.image.entity.Image;
import cat.community.nyangmunity.postImage.image.entity.ImageLike;
import cat.community.nyangmunity.postImage.image.repository.ImageLikeRepository;
import cat.community.nyangmunity.postImage.image.repository.ImageRepository;
import cat.community.nyangmunity.postImage.image.response.ImageLikeResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageLikeCommandService {

	private final ImageLikeQueryService imageQueryService;
	private final ImageRepository imageRepository;

	private final ImageLikeRepository imageLikeRepository;

	@Transactional
	public ImageLikeResponse likeImageProcess(String imageId, Member member) {
		Image image = imageRepository.findById(imageId).orElseThrow(BadRequestException::new);
		Optional<ImageLike> existing = imageQueryService.findImageLike(imageId, member.getId());

		// 이미 좋아요를 누른 상태라면 취소 작업
		if (existing.isPresent()) {
			unlikeImage(existing.get());
			image.decrementLikes();
			return ImageLikeResponse.builder()
				.imageId(imageId)
				.state(false)
				.build();
		}

		// 좋아요를 누른 상태가 아니라면 좋아요 등록
		likeImage(ImageLike.builder()
			.member(member)
			.image(image)
			.build()
		);
		image.incrementLikes();

		return ImageLikeResponse.builder()
			.imageId(imageId)
			.state(true)
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
