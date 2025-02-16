package cat.community.nyangmunity.post.service;

import cat.community.nyangmunity.post.editor.PostEditor;
import cat.community.nyangmunity.post.entity.Post;
import cat.community.nyangmunity.post.entity.PostLike;
import cat.community.nyangmunity.global.exception.UnauthorizedUserException;
import cat.community.nyangmunity.post.repository.PostLikeRepository;
import cat.community.nyangmunity.post.request.PostFormRequest;
import cat.community.nyangmunity.global.exception.BoardNotFoundException;
import cat.community.nyangmunity.post.repository.PostRepository;
import cat.community.nyangmunity.post.request.PostEditRequest;
import cat.community.nyangmunity.post.response.PostImageResponse;
import cat.community.nyangmunity.post.response.PostResponse;
import cat.community.nyangmunity.post.response.PostLikeResponse;
import cat.community.nyangmunity.member.entity.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    public Post getBoard(Long bid) {
        return postRepository.findById(bid).orElseThrow(BoardNotFoundException::new);
    }

    public void write(PostFormRequest postFormRequest, Member member){
        Post post = Post.builder()
                .content(postFormRequest.content())
                .member(member)
                .createDate(LocalDateTime.now())
                .build();

        postRepository.save(post);
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
            throw new UnauthorizedUserException();
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
            throw new UnauthorizedUserException();
        }

        postRepository.delete(post);
    }

    public void like(Long bid, Member member) {
        if(likeCheck(bid, member.getId())) {
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
        Post maxLikePost = postLikeRepository.getMaxLikePost();

        return PostLikeResponse.builder()
                .bid(maxLikePost.getId())
                .boardImages(convertToBoardImageResponse(maxLikePost))
                .nickName(maxLikePost.getMember().getNickname())
                .build();
    }

    private Page<PostResponse> convertToBoardResponse(Page<Post> boardPage) {
        return boardPage.map(board -> PostResponse.from(board, convertToBoardImageResponse(board)));
    }

    private List<PostImageResponse> convertToBoardImageResponse(Post post) {
        return post.getImages().stream()
                .map(PostImageResponse::from)
                .collect(Collectors.toList());
    }

    private boolean isWriter(Long writerId, Long userId) {
        return Objects.equals(writerId, userId);
    }

}
