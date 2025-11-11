package cat.community.nyangmunity.postImage.image.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cat.community.nyangmunity.postImage.image.entity.Image;
import cat.community.nyangmunity.postImage.image.entity.Provider;
import cat.community.nyangmunity.postImage.image.repository.ImageRepository;
import cat.community.nyangmunity.postImage.image.response.UploadImageResponse;
import cat.community.nyangmunity.postImage.image.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageCommandService {

	private final ImageRepository imageRepository;
	private final ImageUtil imageUtil;

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
