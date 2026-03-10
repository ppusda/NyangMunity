package cat.community.nyangmunity.postImage.image.service;

import cat.community.nyangmunity.member.entity.Member;
import cat.community.nyangmunity.postImage.image.entity.Image;
import cat.community.nyangmunity.postImage.image.entity.Provider;
import cat.community.nyangmunity.postImage.image.repository.ImageRepository;
import cat.community.nyangmunity.postImage.image.response.ImageDetailResponse;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageQueryService {

	private final ImageRepository imageRepository;
	private final ImageLikeQueryService imageLikeQueryService;

	/**
	 * 이미지 목록 조회 (태그 포함)
	 */
	public Page<ImageDetailResponse> getImages(
		int page,
		int size,
		Provider provider,
		List<String> tags,
		String sortBy,
		Member member) {

		Pageable pageable = createPageable(page, size, sortBy);

		Page<Image> images;

		if (tags != null && !tags.isEmpty()) {
			// 태그 필터링
			if (provider != null) {
				images = imageRepository.findByProviderAndTags(provider, tags, tags.size(), pageable);
			} else {
				images = imageRepository.findByTags(tags, tags.size(), pageable);
			}
		} else {
			// 전체 조회
			if (provider != null) {
				images = imageRepository.findByProvider(provider, pageable);
			} else {
				images = imageRepository.findAll(pageable);
			}
		}

		// 좋아요 상태 조회 (로그인한 경우)
		if (member != null) {
			List<String> imageIds = images.getContent().stream().map(Image::getId).collect(Collectors.toList());
			List<String> likedImageIds = imageLikeQueryService.getLikedImageIds(member, imageIds);

			return images.map(
				image -> ImageDetailResponse.from(image, likedImageIds.contains(image.getId())));
		} else {
			return images.map(ImageDetailResponse::from);
		}
	}

	/**
	 * Pageable 생성 (정렬 포함)
	 */
	private Pageable createPageable(int page, int size, String sortBy) {
		Sort sort;

		switch (sortBy) {
			case "popular":
				sort = Sort.by(Sort.Direction.DESC, "likesCount");
				break;
			case "views":
				sort = Sort.by(Sort.Direction.DESC, "viewsCount");
				break;
			case "latest":
			default:
				sort = Sort.by(Sort.Direction.DESC, "uploadDate");
				break;
		}

		return PageRequest.of(page, size, sort);
	}

	/**
	 * 이미지 상세 조회
	 */
	public ImageDetailResponse getImage(String imageId, Member member) {
		Image image =
			imageRepository
				.findById(imageId)
				.orElseThrow(() -> new IllegalArgumentException("이미지를 찾을 수 없습니다."));

		boolean likeState = false;
		if (member != null) {
			likeState = imageLikeQueryService.isLiked(member, image);
		}

		return ImageDetailResponse.from(image, likeState);
	}
}