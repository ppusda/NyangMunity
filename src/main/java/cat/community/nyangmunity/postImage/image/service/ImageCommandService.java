package cat.community.nyangmunity.postImage.image.service;

import cat.community.nyangmunity.member.entity.Member;
import cat.community.nyangmunity.postImage.image.entity.Image;
import cat.community.nyangmunity.postImage.image.entity.Provider;
import cat.community.nyangmunity.postImage.image.repository.ImageRepository;
import cat.community.nyangmunity.postImage.image.request.ImageUploadRequest;
import cat.community.nyangmunity.postImage.image.response.ImageDetailResponse;
import cat.community.nyangmunity.postImage.image.response.UploadImageResponse;
import cat.community.nyangmunity.postImage.image.util.ImageUtil;
import cat.community.nyangmunity.tag.service.TagService;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageCommandService {

	private final ImageRepository imageRepository;
	private final ImageUtil imageUtil;
	private final TagService tagService;

	/**
	 * 업로드 할 URL을 반환해주는 메서드
	 * @param filename 파일 이름을 전달 받으면 UUID 파일 이름으로 변환, 이를 DB에 저장
	 * @return 이미지를 업로드 할 수 있는 presigned url, 변환된 이름의 filePath, 할당된 imageId를 반환x
	 */
	@Transactional
	public UploadImageResponse createImageInfo(String filename) {
		String uuid = imageUtil.generateRandomUUID();
		String filePath = imageUtil.createFilepath(uuid, filename);

		Image savedImage = saveImage(Image.builder()
			.id(uuid)
			.name(filename)
			.url(imageUtil.getImageUrl() + filePath)
			.provider(Provider.NYANGMUNITY)
			.build()
		);

		return UploadImageResponse.from(imageUtil.generatePresignedUrl(filePath), savedImage.getId());
	}

	/**
	 * 단일 이미지 업로드를 완료합니다 (설명 및 태그 포함).
	 *
	 * @param request 이미지 업로드 요청 (imageId, description, tags)
	 * @param member 업로드한 회원
	 * @return 생성된 이미지 상세 정보
	 */
	@Transactional
	public ImageDetailResponse completeImageUpload(ImageUploadRequest request, Member member) {
		// 이미지 조회
		Image image =
			imageRepository
				.findById(request.getImageId())
				.orElseThrow(() -> new IllegalArgumentException("이미지를 찾을 수 없습니다."));

		// 업로더 설정
		image.setMember(member);

		// 설명 업데이트
		if (request.getDescription() != null && !request.getDescription().trim().isEmpty()) {
			image.updateDescription(request.getDescription());
		}

		// 태그 추가
		if (request.getTags() != null && !request.getTags().isEmpty()) {
			tagService.addTagsToImage(image, request.getTags());
		}

		return ImageDetailResponse.from(image);
	}

	@Transactional
	public Image saveImage(Image image) {
		return imageRepository.save(image);
	}

	@Transactional
	public void saveImages(List<Image> saveImageList) {
		imageRepository.saveAll(saveImageList);
	}

	@Transactional
	public void deleteImages(List<Image> deleteImageList) {
		imageRepository.deleteAll(deleteImageList);
	}
}
