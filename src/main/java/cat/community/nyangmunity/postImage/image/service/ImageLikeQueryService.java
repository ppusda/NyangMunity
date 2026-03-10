package cat.community.nyangmunity.postImage.image.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cat.community.nyangmunity.global.exception.post.PostNotFoundException;
import cat.community.nyangmunity.member.entity.Member;
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
	public Optional<ImageLike> findImageLike(String imageId, Long memberId) {
		return imageLikeRepository.findByImageIdAndMemberId(imageId, memberId);
	}

	@Transactional(readOnly = true)
	public List<String> fetchLikedImageIds(List<String> imageIds, Member member) {
		return imageLikeRepository.fetchLikedImageIds(imageIds, member);
	}

	/**
	 * 회원이 특정 이미지를 좋아요 했는지 확인
	 */
	public boolean isLiked(Member member, Image image) {
		return imageLikeRepository.existsByMemberAndImage(member, image);
	}

	/**
	 * 회원이 좋아요한 이미지 ID 목록 조회
	 */
	public List<String> getLikedImageIds(Member member, List<String> imageIds) {
		return imageLikeRepository.findByMemberAndImageIdIn(member, imageIds).stream()
			.map(like -> like.getImage().getId())
			.collect(Collectors.toList());
	}
}
