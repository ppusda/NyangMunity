package cat.community.nyangmunity.image.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cat.community.nyangmunity.image.batch.response.ImageResponse;
import cat.community.nyangmunity.image.entity.Image;
import cat.community.nyangmunity.image.entity.Provider;
import cat.community.nyangmunity.image.repository.ImageRepository;
import cat.community.nyangmunity.image.response.UploadImageResponse;
import cat.community.nyangmunity.image.util.ImageUtil;
import cat.community.nyangmunity.post.entity.PostImage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

	private final ImageRepository imageRepository;
	private final ImageUtil imageUtil;

	/***
	 * 업로드 할 URL을 반환해주는 메서드
	 * @param filename 파일 이름을 전달 받으면 UUID 파일 이름으로 변환, 이를 DB에 저장
	 * @return 이미지를 업로드 할 수 있는 presigned url, 변환된 이름의 filePath, 할당된 imageId를 반환
	 */
	@Transactional
	public UploadImageResponse createImageInfo(String filename) {
		String uuid = imageUtil.generateRandomUUID();
		String filePath = imageUtil.createFilepath(uuid, filename);

		Image savedImage = imageRepository.save(
			Image.builder()
				.id(uuid)
				.name(filename)
				.url(imageUtil.getImageUrl() + filePath)
				.provider(Provider.NYANGMUNITY)
				.build()
		);

		return UploadImageResponse.from(imageUtil.generatePresignedUrl(filePath), savedImage.getId());
	}

	@Transactional(readOnly = true)
	public List<Image> findImagesByIds(List<String> imageIds) {
		return imageRepository.findAllByIdIn(imageIds);
	}

	@Transactional(readOnly = true)
	public List<PostImage> findPostImagesByIds(List<String> imageIds) {
		return findImagesByIds(imageIds).stream().map(PostImage::new).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<Image> getAllImages() {
		return imageRepository.findAll();
	}

	@Transactional
	public void saveImages(List<Image> saveImageList) {
		imageRepository.saveAll(saveImageList);
	}

	@Transactional
	public void deleteImages(List<Image> deleteImageList) {
		imageRepository.deleteAll(deleteImageList);
	}

	@Transactional(readOnly = true)
	public Page<ImageResponse> getImageList(int page, Provider provider) {
		Pageable pageable = PageRequest.of(page, 30);
		return imageRepository.findAllByProvider(pageable, provider).map(ImageResponse::from);
	}
}
