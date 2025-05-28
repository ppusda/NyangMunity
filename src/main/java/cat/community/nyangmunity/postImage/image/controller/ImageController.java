package cat.community.nyangmunity.postImage.image.controller;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cat.community.nyangmunity.postImage.image.batch.response.ImageResponse;
import cat.community.nyangmunity.postImage.image.entity.Provider;
import cat.community.nyangmunity.postImage.image.request.ImageLikeRequest;
import cat.community.nyangmunity.postImage.image.response.ProviderResponse;
import cat.community.nyangmunity.postImage.image.response.UploadImageResponse;
import cat.community.nyangmunity.postImage.image.service.ImageQueryService;
import cat.community.nyangmunity.postImage.image.service.ImageCommandService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

	private final ImageQueryService imageQueryService;
	private final ImageCommandService imageCommandService;

	@GetMapping
	public Page<ImageResponse> getImages(@RequestParam int page, @RequestParam Provider provider) {
		return imageQueryService.getImageList(page, provider);
	}

	@GetMapping("/providers")
	public ProviderResponse getProviderList() {
		return new ProviderResponse(Provider.getProviderNames());
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/upload")
	public UploadImageResponse getUploadURL(@RequestParam(name = "filename") String filename) {
		return imageCommandService.createImageInfo(filename);
	}

	@PostMapping("/likes")
	public void likeImage(@RequestBody ImageLikeRequest imageLikeRequest) {
		return;
	}

}
