package cat.community.NyangMunity.board.controller;

import cat.community.NyangMunity.global.exception.EmptyInputValueException;
import cat.community.NyangMunity.global.exception.InvalidRequest;
import cat.community.NyangMunity.user.request.UserSession;
import cat.community.NyangMunity.board.request.BoardForm;
import cat.community.NyangMunity.board.entity.BoardImage;
import cat.community.NyangMunity.board.response.BoardEdit;
import cat.community.NyangMunity.board.request.BoardSearch;
import cat.community.NyangMunity.board.response.BoardResponse;
import cat.community.NyangMunity.board.response.BoardResult;
import cat.community.NyangMunity.board.response.LikeBoardResponse;
import cat.community.NyangMunity.board.service.BoardService;
import cat.community.NyangMunity.board.util.BoardProvider;
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
    public void BoardWrite(@ModelAttribute BoardForm boardForm, UserSession userSession) throws IOException {
        ArrayList<BoardImage> boardImages = new ArrayList<>();

        if(boardForm.getBoardImages() != null){
            boardImages = boardProvider.getImageList(boardForm.getBoardImages());
        }

        if(boardForm.getTitle().isEmpty()){
            throw new EmptyInputValueException();
        }

        try {
            boardService.write(boardForm, boardImages, userSession.id);
        } catch (Exception e) {
            throw new InvalidRequest();
        }

    }

    @GetMapping("/{boardId}")
    public BoardResponse readBoard(@PathVariable(name = "boardId") Long id, UserSession userSession) {
        BoardResponse boardResponse = boardService.read(id);
        if(boardResponse.getUid() == userSession.id) {
            boardResponse.setWriterCheck(true);
        }
        return boardResponse;
    }

    @GetMapping
    public BoardResult readBoards(@ModelAttribute BoardSearch boardSearch){
        return BoardResult.builder()
                .boardList(boardService.getList(boardSearch))
                .totalCnt(boardService.getCount())
                .build();
    }

    @PatchMapping("/{boardId}")
    public void editBoard(@PathVariable Long boardId, @ModelAttribute BoardEdit boardEdit, UserSession userSession) throws IOException {
        ArrayList<BoardImage> boardImages = new ArrayList<>();

        if(boardEdit.getBoardImages() != null){
            boardImages = boardProvider.getImageList(boardEdit.getBoardImages());
        }

        boardService.edit(boardId, boardEdit, boardImages, userSession.id);
    }

    @DeleteMapping("/{boardId}")
    public void delete(@PathVariable Long boardId, UserSession userSession) {
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

//    @GetMapping("/write/{boardId}/rss")
//    public Board getRss(@PathVariable(name = "boardId") Long id) {
//        Board board = boardService.getId();
//        return board;
//    }
}
