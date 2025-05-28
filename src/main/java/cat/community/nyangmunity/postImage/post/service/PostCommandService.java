package cat.community.nyangmunity.postImage.post.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import cat.community.nyangmunity.global.exception.global.BadRequestException;
import cat.community.nyangmunity.global.exception.global.ForbiddenException;
import cat.community.nyangmunity.postImage.image.service.ImageQueryService;
import cat.community.nyangmunity.member.entity.Member;
import cat.community.nyangmunity.postImage.post.editor.PostEditor;
import cat.community.nyangmunity.postImage.post.entity.Post;
import cat.community.nyangmunity.postImage.entity.PostImage;
import cat.community.nyangmunity.postImage.repository.PostImageRepository;
import cat.community.nyangmunity.postImage.post.repository.PostRepository;
import cat.community.nyangmunity.postImage.post.request.PostEditRequest;
import cat.community.nyangmunity.postImage.post.request.PostWriteRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostCommandService {

	private final ImageQueryService imageQueryService;
	private final PostQueryService postQueryService;

	private final PostRepository postRepository;
	private final PostImageRepository postImageRepository;

	@Transactional
	public void write(PostWriteRequest postWriteRequest, Member member) {
		if (postWriteRequest.postImageIds().isEmpty()) {
			throw new BadRequestException("이미지는 필수로 입력해야 합니다.");
		}

		Post savedPost = postRepository.save(
			Post.builder()
				.content(postWriteRequest.content())
				.member(member)
				.createDate(LocalDateTime.now())
				.build()
		);

		List<PostImage> postImages = imageQueryService.findPostImagesByIds(postWriteRequest.postImageIds());
		savedPost.updatePostImages(postImages);
		postImageRepository.saveAll(postImages);
	}

	@Transactional
	public void edit(Long postId, PostEditRequest postEditRequest, Long memberId) {
		Post post = postQueryService.findPostById(postId);

		if (!post.getMember().getId().equals(memberId)) {
			throw new ForbiddenException();
		}

		postRepository.save(post);

		PostEditor.PostEditorBuilder postEditorBuilder = post.toEditor();
		PostEditor postEditor = postEditorBuilder
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
