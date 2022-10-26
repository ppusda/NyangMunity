package cat.community.NyangMunity.controller;

import cat.community.NyangMunity.controller.form.BoardForm;
import cat.community.NyangMunity.domain.Board;
import cat.community.NyangMunity.response.BoardResponse;
import cat.community.NyangMunity.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping("/write")
    public void BoardWrite(@RequestBody @Valid BoardForm boardForm) {
        boardService.write(boardForm);
    }

    @GetMapping("/read/{boardId}")
    public BoardResponse readBorad(@PathVariable(name = "boardId") Long id) {
        BoardResponse boardResponse = boardService.read(id);
        return boardResponse;
    }

    @GetMapping("/read/boards")
    public List<BoardResponse> readBoards(){
        return boardService.getBoard();
    }

//    @GetMapping("/write/{boardId}/rss")
//    public Board getRss(@PathVariable(name = "boardId") Long id) {
//        Board board = boardService.getId();
//        return board;
//    }
}
