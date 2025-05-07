package cat.community.nyangmunity.post.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cat.community.nyangmunity.global.exception.post.PostNotFoundException;
import cat.community.nyangmunity.post.entity.Post;
import cat.community.nyangmunity.post.repository.PostLikeRepository;
import cat.community.nyangmunity.post.repository.PostRepository;
import cat.community.nyangmunity.post.response.PostImageResponse;
import cat.community.nyangmunity.post.response.PostLikeResponse;
import cat.community.nyangmunity.post.response.PostResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostQueryService {

	private final PostRepository postRepository;
	private final PostLikeRepository postLikeRepository;

	@Transactional(readOnly = true)
	public Post findPostById(Long bid) {
		return postRepository.findById(bid).orElseThrow(PostNotFoundException::new);
	}

	@Transactional(readOnly = true)
	public Page<PostResponse> getPosts(Integer page, Integer size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Post> boards = postRepository.findAllByOrderByCreateDateDesc(pageable);

		return convertToBoardResponse(boards);
	}

	@Transactional(readOnly = true)
	public PostLikeResponse maxLikePost() {
		try {
			Post post = postLikeRepository.getMaxLikePost();
			return PostLikeResponse.builder()
				.id(post.getId())
				.postImages(convertToBoardImageResponse(post))
				.nickname(post.getMember().getNickname())
				.message("가장 인기 많은 " + post.getMember().getNickname() + "님의 이미지 입니다!")
				.build();
		} catch (PostNotFoundException e) {
			return PostLikeResponse.builder()
				.message(e.getMessage())
				.build();
		}
	}

	private Page<PostResponse> convertToBoardResponse(Page<Post> boardPage) {
		return boardPage.map(board -> PostResponse.from(board, convertToBoardImageResponse(board)));
	}

	private List<PostImageResponse> convertToBoardImageResponse(Post post) {
		return post.getImages().stream()
			.map(PostImageResponse::from)
			.collect(Collectors.toList());
	}
}
