package cat.community.NyangMunity.board.controller;

import cat.community.NyangMunity.board.request.BoardFormRequest;
import cat.community.NyangMunity.board.entity.BoardImage;
import cat.community.NyangMunity.board.request.BoardEditRequest;
import cat.community.NyangMunity.board.request.BoardListRequest;
import cat.community.NyangMunity.board.response.BoardResponse;
import cat.community.NyangMunity.board.response.LikeBoardResponse;
import cat.community.NyangMunity.board.service.BoardService;
import cat.community.NyangMunity.board.util.BoardProvider;
import cat.community.NyangMunity.user.service.UserService;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;


@Slf4j
@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final UserService userService;
    private final BoardService boardService;
    private final BoardProvider boardProvider;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/write")
    public void writeBoard(@ModelAttribute @Valid BoardFormRequest boardFormRequest, Principal principal) throws IOException {
        ArrayList<BoardImage> boardImages = new ArrayList<>();

        if(boardFormRequest.boardImages() != null){
            boardImages = boardProvider.getImageList(boardFormRequest.boardImages());
        }

        boardService.write(boardFormRequest, boardImages, userService.getUserById(Long.parseLong(principal.getName())));
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
    public void editBoard(@PathVariable Long boardId, @ModelAttribute @Valid BoardEditRequest boardEditRequest, Principal principal) throws IOException {
        ArrayList<BoardImage> boardImages = new ArrayList<>();

        if(boardEditRequest.boardImages() != null){
            boardImages = boardProvider.getImageList(boardEditRequest.boardImages());
        }

        boardService.edit(boardId, boardEditRequest, boardImages, Long.parseLong(principal.getName()));
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
