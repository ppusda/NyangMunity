package cat.community.nyangmunity.postImage.post.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cat.community.nyangmunity.global.exception.post.PostNotFoundException;
import cat.community.nyangmunity.postImage.post.entity.Post;
import cat.community.nyangmunity.postImage.post.repository.PostRepository;
import cat.community.nyangmunity.postImage.post.response.PostResponse;
import cat.community.nyangmunity.postImage.response.PostImageResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostQueryService {

	private final PostRepository postRepository;

	@Transactional(readOnly = true)
	public Post findPostById(Long bid) {
		return postRepository.findById(bid).orElseThrow(PostNotFoundException::new);
	}

	@Transactional(readOnly = true)
	public Page<PostResponse> getPosts(Integer page, Integer size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Post> posts = postRepository.findAllByOrderByCreateDateDesc(pageable);

		return convertToPostResponse(posts);
	}

	private Page<PostResponse> convertToPostResponse(Page<Post> postPage) {
		return postPage.map(post -> PostResponse.from(post, convertToPostImageResponse(post)));
	}

	private List<PostImageResponse> convertToPostImageResponse(Post post) {
		return post.getImages().stream()
			.map(PostImageResponse::from)
			.collect(Collectors.toList());
	}
}
