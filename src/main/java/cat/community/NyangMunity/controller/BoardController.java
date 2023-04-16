package cat.community.NyangMunity.controller;

import cat.community.NyangMunity.controller.form.BoardForm;
import cat.community.NyangMunity.domain.Board;
import cat.community.NyangMunity.domain.BoardEditor;
import cat.community.NyangMunity.request.BoardEdit;
import cat.community.NyangMunity.request.BoardSearch;
import cat.community.NyangMunity.response.BoardResponse;
import cat.community.NyangMunity.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.mail.Multipart;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping("/boards/write")
    public void BoardWrite(@ModelAttribute BoardForm boardForm) throws IOException {
        log.info(String.valueOf(boardForm.getTitle()));
        log.info(String.valueOf(boardForm.getContent()));
        for(MultipartFile file : boardForm.getImgInput()){
            log.info(String.valueOf(file));

            byte[] bytes = file.getBytes();
            Path path = Paths.get("D:\\NyangMunityImages\\" + file.getOriginalFilename());
            Files.write(path, bytes);
        }
        //boardService.write(boardForm);
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