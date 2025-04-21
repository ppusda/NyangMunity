package cat.community.nyangmunity.post.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import cat.community.nyangmunity.global.exception.global.ForbiddenException;
import cat.community.nyangmunity.global.exception.post.PostNotFoundException;
import cat.community.nyangmunity.image.service.ImageService;
import cat.community.nyangmunity.member.entity.Member;
import cat.community.nyangmunity.post.editor.PostEditor;
import cat.community.nyangmunity.post.entity.Post;
import cat.community.nyangmunity.post.entity.PostImage;
import cat.community.nyangmunity.post.entity.PostLike;
import cat.community.nyangmunity.post.repository.PostImageRepository;
import cat.community.nyangmunity.post.repository.PostLikeRepository;
import cat.community.nyangmunity.post.repository.PostRepository;
import cat.community.nyangmunity.post.request.PostEditRequest;
import cat.community.nyangmunity.post.request.PostWriteRequest;
import cat.community.nyangmunity.post.response.PostImageResponse;
import cat.community.nyangmunity.post.response.PostLikeResponse;
import cat.community.nyangmunity.post.response.PostResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

	private final ImageService imageService;

	private final PostRepository postRepository;
	private final PostLikeRepository postLikeRepository;
	private final PostImageRepository postImageRepository;

	public Post getBoard(Long bid) {
		return postRepository.findById(bid).orElseThrow(PostNotFoundException::new);
	}

	@Transactional
	public void write(PostWriteRequest postWriteRequest, Member member) {
		Post savedPost = postRepository.save(
			Post.builder()
				.content(postWriteRequest.content())
				.member(member)
				.createDate(LocalDateTime.now())
				.build()
		);

		List<PostImage> postImages = imageService.findPostImagesByIds(postWriteRequest.postImageIds());
		savedPost.updatePostImages(postImages);
		postImageRepository.saveAll(postImages);
	}

	public PostResponse read(Long bid) {
		Post post = getBoard(bid);
		return PostResponse.from(post, convertToBoardImageResponse(post));
	}

	public Page<PostResponse> getList(Integer page, Integer size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Post> boards = postRepository.findAllByOrderByCreateDateDesc(pageable);

		return convertToBoardResponse(boards);
	}

	@Transactional
	public void edit(Long bid, PostEditRequest postEditRequest, Long uid) {
		Post post = getBoard(bid);

		if (!post.getMember().getId().equals(uid)) {
			throw new ForbiddenException();
		}

		postRepository.save(post);

		PostEditor.BoardEditorBuilder boardEditorBuilder = post.toEditor();
		PostEditor postEditor = boardEditorBuilder
			.content(postEditRequest.content())
			.build();

		post.edit(postEditor);
	}

	@Transactional
	public void delete(Long bid, Long uid) {
		Post post = getBoard(bid);

		if (!post.getMember().getId().equals(uid)) {
			throw new ForbiddenException();
		}

		postRepository.delete(post);
	}

	public void like(Long bid, Member member) {
		if (likeCheck(bid, member.getId())) {
			postLikeRepository.deleteByPostIdAndMemberId(bid, member.getId());
			return;
		}

		Post post = getBoard(bid);

		PostLike postLike = PostLike.builder()
			.post(post)
			.member(member)
			.build();

		postLikeRepository.save(postLike);
	}

	public boolean likeCheck(Long bid, Long uid) {
		return postLikeRepository.findByPostIdAndMemberId(bid, uid).isPresent();
	}

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

	private boolean isWriter(Long writerId, Long memberId) {
		return Objects.equals(writerId, memberId);
	}

}
