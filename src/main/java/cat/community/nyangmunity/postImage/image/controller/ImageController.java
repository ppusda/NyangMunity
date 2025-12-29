package cat.community.nyangmunity.postImage.image.controller;

import cat.community.nyangmunity.member.entity.Member;
import cat.community.nyangmunity.postImage.image.batch.response.ImageResponse;
import cat.community.nyangmunity.postImage.image.entity.Provider;
import cat.community.nyangmunity.postImage.image.request.ImageUploadRequest;
import cat.community.nyangmunity.postImage.image.response.ImageDetailResponse;
import cat.community.nyangmunity.postImage.image.response.ProviderResponse;
import cat.community.nyangmunity.postImage.image.response.UploadImageResponse;
import cat.community.nyangmunity.postImage.image.service.ImageCommandService;
import cat.community.nyangmunity.postImage.image.service.ImageQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

	/**
	 * 단일 이미지 업로드 완료 API (설명 및 태그 포함)
	 *
	 * @param request 이미지 업로드 요청
	 * @param member 인증된 회원
	 * @return 생성된 이미지 상세 정보
	 */
	@PreAuthorize("isAuthenticated()")
	@PostMapping
	public ResponseEntity<ImageDetailResponse> uploadImage(
			@RequestBody ImageUploadRequest request, @AuthenticationPrincipal Member member) {
		ImageDetailResponse response = imageCommandService.completeImageUpload(request, member);
		return ResponseEntity.ok(response);
	}
}
