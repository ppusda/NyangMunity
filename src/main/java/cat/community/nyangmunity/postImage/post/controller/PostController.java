package cat.community.nyangmunity.postImage.post.controller;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cat.community.nyangmunity.member.service.MemberQueryService;
import cat.community.nyangmunity.postImage.post.request.PostEditRequest;
import cat.community.nyangmunity.postImage.post.request.PostWriteRequest;
import cat.community.nyangmunity.postImage.post.request.PostsRequest;
import cat.community.nyangmunity.postImage.post.response.PostResponse;
import cat.community.nyangmunity.postImage.post.service.PostCommandService;
import cat.community.nyangmunity.postImage.post.service.PostQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

	private final PostCommandService postCommandService;
	private final PostQueryService postQueryService;
	private final MemberQueryService memberQueryService;

	@GetMapping
	public Page<PostResponse> readPosts(@ModelAttribute PostsRequest postsRequest, Principal principal) {
		if (principal != null) {
			return postQueryService.getPosts(postsRequest.getPage(), postsRequest.getSize(),
				memberQueryService.findMemberById(Long.parseLong(principal.getName())));
		}

		return postQueryService.getPosts(postsRequest.getPage(), postsRequest.getSize(), null);
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping
	public void writePost(@RequestBody @Validated PostWriteRequest postWriteRequest, Principal principal) {
		postCommandService.write(postWriteRequest,
			memberQueryService.findMemberById(Long.parseLong(principal.getName())));
	}

	@PreAuthorize("isAuthenticated()")
	@PatchMapping("/{postId}")
	public void editPost(@PathVariable Long postId, @ModelAttribute @Valid PostEditRequest postEditRequest,
		Principal principal) {
		postCommandService.edit(postId, postEditRequest, Long.parseLong(principal.getName()));
	}

	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/{postId}")
	public void deletePost(@PathVariable Long postId, Principal principal) {
		postCommandService.delete(postId, Long.parseLong(principal.getName()));
	}
}
