package cat.community.NyangMunity.board.controller;

import cat.community.NyangMunity.global.exception.EmptyInputValueException;
import cat.community.NyangMunity.global.exception.InvalidRequest;
import cat.community.NyangMunity.user.request.UserSession;
import cat.community.NyangMunity.board.request.BoardFormRequest;
import cat.community.NyangMunity.board.entity.BoardImage;
import cat.community.NyangMunity.board.request.BoardEditRequest;
import cat.community.NyangMunity.board.request.BoardListRequest;
import cat.community.NyangMunity.board.response.BoardResponse;
import cat.community.NyangMunity.board.response.BoardListResponse;
import cat.community.NyangMunity.board.response.LikeBoardResponse;
import cat.community.NyangMunity.board.service.BoardService;
import cat.community.NyangMunity.board.util.BoardProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;


@Slf4j
@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final BoardProvider boardProvider;

    @PostMapping("/write")
    public void writeBoard(@ModelAttribute @Valid BoardFormRequest boardFormRequest, UserSession userSession) throws IOException {
        ArrayList<BoardImage> boardImages = new ArrayList<>();

        if(boardFormRequest.boardImages() != null){
            boardImages = boardProvider.getImageList(boardFormRequest.boardImages());
        }

        if(boardFormRequest.title().isEmpty()){
            throw new EmptyInputValueException();
        }

        try {
            boardService.write(boardFormRequest, boardImages, userSession.id);
        } catch (Exception e) {
            throw new InvalidRequest();
        }

    }

    @GetMapping("/{boardId}")
    public BoardResponse readBoard(@PathVariable(name = "boardId") Long id, UserSession userSession) {
        BoardResponse boardResponse = boardService.read(id, userSession.id);
        return boardResponse;
    }

    @GetMapping
    public BoardListResponse readBoards(@ModelAttribute BoardListRequest boardListRequest){
        return BoardListResponse.builder()
                .boardList(boardService.getList(boardListRequest))
                .totalCnt(boardService.getCount())
                .build();
    }

    @PatchMapping("/{boardId}")
    public void editBoard(@PathVariable Long boardId, @ModelAttribute @Valid BoardEditRequest boardEditRequest, UserSession userSession) throws IOException {
        ArrayList<BoardImage> boardImages = new ArrayList<>();

        if(boardEditRequest.boardImages() != null){
            boardImages = boardProvider.getImageList(boardEditRequest.boardImages());
        }

        boardService.edit(boardId, boardEditRequest, boardImages, userSession.id);
    }

    @DeleteMapping("/{boardId}")
    public void deleteBoard(@PathVariable Long boardId, UserSession userSession) {
        boardService.delete(boardId, userSession.id);
    }

    @PostMapping("/like/{boardId}")
    public void boardLike(@PathVariable(name = "boardId") Long bid, UserSession userSession){
        boardService.like(bid, userSession.id);
    }

    @PostMapping("/like/check/{boardId}")
    public boolean boardLikeCheck(@PathVariable(name = "boardId") Long bid, UserSession userSession){
        return boardService.likeCheck(bid, userSession.id);
    }

    @PostMapping("/like")
    public LikeBoardResponse maxLikeBoard() {
        return boardService.maxLikeBoard();
    }

}
