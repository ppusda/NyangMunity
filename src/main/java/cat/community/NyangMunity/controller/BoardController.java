package cat.community.NyangMunity.controller;

import cat.community.NyangMunity.controller.form.BoardForm;
import cat.community.NyangMunity.domain.Board;
import cat.community.NyangMunity.domain.BoardEditor;
import cat.community.NyangMunity.request.BoardEdit;
import cat.community.NyangMunity.request.BoardSearch;
import cat.community.NyangMunity.response.BoardResponse;
import cat.community.NyangMunity.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping("/boards/write")
    public void BoardWrite(@RequestBody @Valid BoardForm boardForm) {
        boardService.write(boardForm);
    }

    @GetMapping("/boards/{boardId}")
    public BoardResponse readBoard(@PathVariable(name = "boardId") Long id) {
        BoardResponse boardResponse = boardService.read(id);
        return boardResponse;
    }

    @GetMapping("/boards")
    public List<BoardResponse> readBoards(@ModelAttribute BoardSearch boardSearch){
        return boardService.getList(boardSearch);
    } //pageableDefault는 기본 size값이 10이다.

    @PatchMapping("/boards/{boardId}")
    public void editBoard(@PathVariable Long boardId, @RequestBody @Valid BoardEdit request) {
        boardService.edit(boardId, request);
    }

    @DeleteMapping("/boards/{boardId}")
    public void delete(@PathVariable Long boardId) {
        boardService.delete(boardId);
    }

//    @GetMapping("/write/{boardId}/rss")
//    public Board getRss(@PathVariable(name = "boardId") Long id) {
//        Board board = boardService.getId();
//        return board;
//    }
}
