package cat.community.nyangmunity.post.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import cat.community.nyangmunity.global.exception.global.ForbiddenException;
import cat.community.nyangmunity.image.service.ImageService;
import cat.community.nyangmunity.member.entity.Member;
import cat.community.nyangmunity.post.editor.PostEditor;
import cat.community.nyangmunity.post.entity.Post;
import cat.community.nyangmunity.post.entity.PostImage;
import cat.community.nyangmunity.post.repository.PostImageRepository;
import cat.community.nyangmunity.post.repository.PostRepository;
import cat.community.nyangmunity.post.request.PostEditRequest;
import cat.community.nyangmunity.post.request.PostWriteRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostCommandService {

	private final ImageService imageService;

	private final PostRepository postRepository;
	private final PostQueryService postQueryService;
	private final PostImageRepository postImageRepository;

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

	@Transactional
	public void edit(Long postId, PostEditRequest postEditRequest, Long uid) {
		Post post = postQueryService.findPostById(postId);

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
	public void delete(Long postId, Long memberId) {
		Post post = postQueryService.findPostById(postId);

		if (!post.getMember().getId().equals(memberId)) {
			throw new ForbiddenException();
		}

		postRepository.delete(post);
	}
}
