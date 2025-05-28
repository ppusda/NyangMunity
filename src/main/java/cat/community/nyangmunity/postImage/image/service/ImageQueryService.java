package cat.community.nyangmunity.postImage.image.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cat.community.nyangmunity.postImage.image.batch.response.ImageResponse;
import cat.community.nyangmunity.postImage.image.entity.Image;
import cat.community.nyangmunity.postImage.image.entity.Provider;
import cat.community.nyangmunity.postImage.image.repository.ImageRepository;
import cat.community.nyangmunity.postImage.entity.PostImage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageQueryService {

	private final ImageRepository imageRepository;

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

	@Transactional(readOnly = true)
	public Page<ImageResponse> getImageList(int page, Provider provider) {
		Pageable pageable = PageRequest.of(page, 30);
		return imageRepository.findAllByProvider(pageable, provider).map(ImageResponse::from);
	}

}
