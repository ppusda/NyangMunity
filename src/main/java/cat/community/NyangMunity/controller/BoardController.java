package cat.community.NyangMunity.controller;

import cat.community.NyangMunity.controller.form.BoardForm;
import cat.community.NyangMunity.domain.Board;
import cat.community.NyangMunity.domain.BoardEditor;
import cat.community.NyangMunity.domain.BoardImage;
import cat.community.NyangMunity.domain.BoardLike;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/nm")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final String PATH = "C:\\Users\\ppusd\\Pictures\\NyangMunityImages"; // 배포 시 변경 (?)
    // private final String PATH = "/home/ec2-user/nm/images/";

    @PostMapping("/boards/write")
    public void BoardWrite(@ModelAttribute BoardForm boardForm) throws IOException {
        ArrayList<BoardImage> boardImages = new ArrayList<>();

        if(boardForm.getImgInput() != null){
            // todo file service 향후 적용 해보자
            for(MultipartFile file : boardForm.getImgInput()){
                BoardImage boardImage = BoardImage.builder()
                        .name(file.getOriginalFilename())
                        .path(PATH+ file.getOriginalFilename())
                        .size(file.getSize())
                        .build();

                byte[] bytes = file.getBytes();
                Path path = Paths.get(PATH + file.getOriginalFilename());
                Files.write(path, bytes);
                log.info("Path: " + PATH + file.getOriginalFilename() + "에 저장 완료했습니다.");

                boardImages.add(boardImage);
            }
        }

        boardService.write(boardForm, boardImages);
    }

    @GetMapping("/boards/{boardId}")
    public BoardResponse readBoard(@PathVariable(name = "boardId") Long id) {
        BoardResponse boardResponse = boardService.read(id);
        return boardResponse;
    }

    @GetMapping("/boards")
    public List<BoardResponse> readBoards(@ModelAttribute BoardSearch boardSearch){
        return boardService.getList(boardSearch);
    }

    @PatchMapping("/boards/{boardId}")
    public void editBoard(@PathVariable Long boardId, @RequestBody @Valid BoardEdit request) {
        boardService.edit(boardId, request);
    }

    @DeleteMapping("/boards/{boardId}")
    public void delete(@PathVariable Long boardId) {
        boardService.delete(boardId);
    }

    @PostMapping("/boards/like/{boardId}")
    public void boardLike(@PathVariable(name = "boardId") Long id ){
//        BoardLike boardLike = boardService.like(id);
    }

//    @GetMapping("/write/{boardId}/rss")
//    public Board getRss(@PathVariable(name = "boardId") Long id) {
//        Board board = boardService.getId();
//        return board;
//    }
}
