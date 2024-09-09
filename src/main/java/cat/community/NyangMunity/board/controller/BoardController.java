package cat.community.NyangMunity.board.controller;

import cat.community.NyangMunity.board.request.BoardFormRequest;
import cat.community.NyangMunity.board.request.BoardEditRequest;
import cat.community.NyangMunity.board.request.BoardListRequest;
import cat.community.NyangMunity.board.response.BoardResponse;
import cat.community.NyangMunity.board.response.LikeBoardResponse;
import cat.community.NyangMunity.board.service.BoardService;
import cat.community.NyangMunity.user.service.UserService;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final UserService userService;
    private final BoardService boardService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/write")
    public void writeBoard(@ModelAttribute @Valid BoardFormRequest boardFormRequest, Principal principal) {
        boardService.write(boardFormRequest, userService.getUserById(Long.parseLong(principal.getName())));
    }

    @GetMapping("/{boardId}")
    public BoardResponse readBoard(@PathVariable(name = "boardId") Long id) {
        return boardService.read(id);
    }

    @GetMapping
    public Page<BoardResponse> readBoards(@ModelAttribute BoardListRequest boardListRequest){
        return boardService.getList(boardListRequest.getPage(), boardListRequest.getSize());
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{boardId}")
    public void editBoard(@PathVariable Long boardId, @ModelAttribute @Valid BoardEditRequest boardEditRequest, Principal principal) {
        boardService.edit(boardId, boardEditRequest, Long.parseLong(principal.getName()));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{boardId}")
    public void deleteBoard(@PathVariable Long boardId, Principal principal) {
        boardService.delete(boardId, Long.parseLong(principal.getName()));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/like/{boardId}")
    public void boardLike(@PathVariable(name = "boardId") Long bid, Principal principal){
        boardService.like(bid, userService.getUserById(Long.parseLong(principal.getName())));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/like/check/{boardId}")
    public boolean boardLikeCheck(@PathVariable(name = "boardId") Long bid, Principal principal){
        return boardService.likeCheck(bid, Long.parseLong(principal.getName()));
    }

    @PostMapping("/like")
    public LikeBoardResponse maxLikeBoard() {
        return boardService.maxLikeBoard();
    }

}
