package cat.community.nyangmunity.postImage.post.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cat.community.nyangmunity.global.exception.post.PostNotFoundException;
import cat.community.nyangmunity.member.entity.Member;
import cat.community.nyangmunity.postImage.image.entity.Image;
import cat.community.nyangmunity.postImage.image.service.ImageLikeQueryService;
import cat.community.nyangmunity.postImage.post.entity.Post;
import cat.community.nyangmunity.postImage.post.repository.PostRepository;
import cat.community.nyangmunity.postImage.post.response.PostResponse;
import cat.community.nyangmunity.postImage.response.PostImageResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostQueryService {

	private final PostRepository postRepository;
	private final ImageLikeQueryService imageLikeQueryService;

	@Transactional(readOnly = true)
	public Post findPostById(Long bid) {
		return postRepository.findById(bid).orElseThrow(PostNotFoundException::new);
	}

	@Transactional(readOnly = true)
	public Page<PostResponse> getPosts(Integer page, Integer size, Member member) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Post> posts = postRepository.findAllByOrderByCreateDateDesc(pageable);

		// 중복되지 않는 Image ID 수집
		List<String> imageIds = posts.getContent().stream()
			.flatMap(p -> p.getPostImages().stream())
			.map(pi -> pi.getImage().getId())
			.distinct()
			.toList();

		// 로그인 했다면 유저가 좋아요 한 이미지 ID 조회 (QueryDSL)
		Set<String> likedImageIds = member != null
			? new HashSet<>(imageLikeQueryService.fetchLikedImageIds(imageIds, member))
			: Collections.emptySet();

		return convertToPostResponse(posts, likedImageIds);
	}

	private Page<PostResponse> convertToPostResponse(Page<Post> postPage, Set<String> likedImageIds) {
		return postPage.map(post -> {
			List<PostImageResponse> images = post.getPostImages().stream()
				.map(pi -> {
					Image img = pi.getImage();
					boolean liked = likedImageIds.contains(img.getId());
					return new PostImageResponse(img.getId(), img.getUrl(), liked);
				})
				.toList();

			return PostResponse.from(post, images);
		});
	}
}
