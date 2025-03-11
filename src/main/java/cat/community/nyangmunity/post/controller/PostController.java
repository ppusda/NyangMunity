package cat.community.nyangmunity.post.controller;

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

import cat.community.nyangmunity.post.request.PostEditRequest;
import cat.community.nyangmunity.post.request.PostWriteRequest;
import cat.community.nyangmunity.post.request.PostsRequest;
import cat.community.nyangmunity.post.response.PostResponse;
import cat.community.nyangmunity.post.response.PostLikeResponse;
import cat.community.nyangmunity.post.service.PostService;
import cat.community.nyangmunity.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final MemberService memberService;
    private final PostService postService;

    @PostMapping
    public void writePost(@RequestBody @Validated PostWriteRequest postWriteRequest, Principal principal) {
        postService.write(postWriteRequest, memberService.findMemberById(Long.parseLong(principal.getName())));
    }

    @GetMapping("/{postId}")
    public PostResponse readPost(@PathVariable(name = "postId") Long id) {
        return postService.read(id);
    }

    @GetMapping
    public Page<PostResponse> readPosts(@ModelAttribute PostsRequest postsRequest){
        return postService.getList(postsRequest.getPage(), postsRequest.getSize());
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{postId}")
    public void editPost(@PathVariable Long postId, @ModelAttribute @Valid PostEditRequest postEditRequest, Principal principal) {
        postService.edit(postId, postEditRequest, Long.parseLong(principal.getName()));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable Long postId, Principal principal) {
        postService.delete(postId, Long.parseLong(principal.getName()));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/like/{postId}")
    public void postLike(@PathVariable(name = "postId") Long bid, Principal principal){
        postService.like(bid, memberService.findMemberById(Long.parseLong(principal.getName())));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/like/check/{postId}")
    public boolean postLikeCheck(@PathVariable(name = "postId") Long bid, Principal principal){
        return postService.likeCheck(bid, Long.parseLong(principal.getName()));
    }

    @PostMapping("/like")
    public PostLikeResponse maxLikePost() {
        return postService.maxLikePost();
    }
}
