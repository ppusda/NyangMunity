package cat.community.nyangmunity.postImage.image.controller;

import java.security.Principal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import cat.community.nyangmunity.member.service.MemberQueryService;
import cat.community.nyangmunity.postImage.image.request.ImageLikeRequest;
import cat.community.nyangmunity.postImage.response.MaxLikePostImageResponse;
import cat.community.nyangmunity.postImage.image.service.ImageLikeCommandService;
import cat.community.nyangmunity.postImage.image.service.ImageLikeQueryService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ImageLikeController {

	private final MemberQueryService memberQueryService;
	private final ImageLikeQueryService imageQueryService;

	private final ImageLikeCommandService imageCommandService;

	@GetMapping("/likes")
	public MaxLikePostImageResponse maxLikePost() {
		return imageQueryService.getMaxLikeImage();
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/likes")
	public void likeImage(@RequestBody ImageLikeRequest imageLikeRequest, Principal principal) {
		imageCommandService.likeImageProcess(imageLikeRequest.imageId(),
			memberQueryService.findMemberById(Long.parseLong(principal.getName()))
		);
	}
}
