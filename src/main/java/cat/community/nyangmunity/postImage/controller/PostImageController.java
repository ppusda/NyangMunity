package cat.community.nyangmunity.postImage.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import cat.community.nyangmunity.postImage.image.request.ImageLikeRequest;
import cat.community.nyangmunity.postImage.post.service.PostQueryService;
import cat.community.nyangmunity.postImage.response.MaxLikePostImageResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PostImageController {

	private final PostQueryService postQueryService;

	@GetMapping("/likes")
	public MaxLikePostImageResponse maxLikePost() {
		return postQueryService.maxLikePost();
	}

	@PostMapping("/likes")
	public void likeImage(@RequestBody ImageLikeRequest imageLikeRequest) {
		return;
	}
}
