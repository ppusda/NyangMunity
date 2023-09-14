package cat.community.NyangMunity.service;

import cat.community.NyangMunity.request.BoardForm;
import cat.community.NyangMunity.domain.Board;
import cat.community.NyangMunity.domain.BoardImage;
import cat.community.NyangMunity.exception.PostNotFound;
import cat.community.NyangMunity.repository.BoardRepository;
import cat.community.NyangMunity.request.BoardEdit;
import cat.community.NyangMunity.request.BoardSearch;
import cat.community.NyangMunity.response.BoardResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Transactional
@SpringBootTest
class BoardServiceTest {

    @Autowired
    private BoardService boardService;

    @Autowired
    private BoardRepository boardRepository;

    @BeforeEach
    void clean() {
        boardRepository.deleteAll();
    }


// bootJar 실패로 인한 이전 테스트 내역 주석 처리
ArrayList<BoardImage> boardImages = new ArrayList<>();

    @Test
    @DisplayName("글 작성")
    void test1(){
        // given
        BoardForm boardForm = BoardForm.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        // when
        boardService.write(boardForm, boardImages);

        // then
        assertEquals(1L, boardRepository.count());
        Board board = boardRepository.findAll().get(0);
        assertEquals("제목입니다.", board.getTitle());
        assertEquals("내용입니다.", board.getContent());
    }

    @Test
    @DisplayName("글 1개 조회")
    void test2() {
        // given
        Board bd = Board.builder()
                .title("foo")
                .content("bar")
                .boardImages(boardImages)
                .build();
        boardRepository.save(bd);

        // when
        BoardResponse response = boardService.read(bd.getId());

        // then
        assertThrows(PostNotFound.class, () -> {
            boardService.read(bd.getId() + 1L);
        });
    }

    @Test
    @DisplayName("글 여러개 조회")
    void test3() throws Exception {
        // given
        boardRepository.saveAll(List.of(
                Board.builder()
                        .title("title_1")
                        .content("content_1")
                        .boardImages(boardImages)
                        .build(),
                Board.builder()
                        .title("title_2")
                        .content("content_2")
                        .boardImages(boardImages)
                        .build()
        )); // 한번에 저장

        BoardSearch boardSearch = BoardSearch.builder()
                .page(1)
                .build();

        // when
        List<BoardResponse> boardList = boardService.getList(boardSearch);

        // then
        assertEquals(2L, boardList.size());
    }

    @Test
    @DisplayName("글 1페이지 조회")
    void test4() throws Exception {
        // given
        List<Board> requestBoards = IntStream.range(0, 20)
                .mapToObj(i -> Board.builder()
                        .title("빵국이 제목 " + i)
                        .content("빵국이 입니다 " + i)
                        .boardImages(boardImages)
                        .build())
                .collect(Collectors.toList());

        boardRepository.saveAll(requestBoards); // 한번에 저장

        BoardSearch boardSearch = BoardSearch.builder()
                .page(1)
                .size(10)
                .build();

        // when
        List<BoardResponse> boardList = boardService.getList(boardSearch);

        // then
        assertEquals(10L, boardList.size());
        assertEquals("빵국이 제목 19", boardList.get(0).getTitle());
    }

    @Test
    @DisplayName("글 제목 수정")
    void test5() throws Exception {
        // given
        Board board = Board.builder()
                        .title("빵국이제목")
                        .content("빵국입니다")
                        .build();

        boardRepository.save(board); // 한번에 저장

        BoardEdit boardEdit = BoardEdit.builder()
                                .title("제목: 빵국이")
                                .build();

        // when
        boardService.edit(board.getId(), boardEdit);

        // then
        Board changedBoard = boardRepository.findById(board.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다."));
        Assertions.assertEquals("제목: 빵국이", changedBoard.getTitle());
    }

    @Test
    @DisplayName("글 내용 수정")
    void test6() throws Exception {
        // given
        Board board = Board.builder()
                .title("빵국이 제목")
                .content("빵국입니다")
                .build();

        boardRepository.save(board); // 한번에 저장

        BoardEdit boardEdit = BoardEdit.builder()
                .title(null) // null 값 처리 시에는 Builder 클래스를 새로 생성하여 만들면 된다.
                .content("빵국이 내용")
                .build();

        // when
        boardService.edit(board.getId(), boardEdit);

        // then
        Board changedBoard = boardRepository.findById(board.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다."));
        Assertions.assertEquals("빵국이 제목", changedBoard.getTitle());
        Assertions.assertEquals("빵국이 내용", changedBoard.getContent());
    }

    @Test
    @DisplayName("게시글 삭제")
    void test7() throws Exception {
        // given
        Board board = Board.builder()
                .title("빵국이 제목")
                .content("빵국입니다")
                .build();

        boardRepository.save(board);

        // when
        boardService.delete(board.getId());

        // then
        Assertions.assertEquals(0, boardRepository.count());
    }

    @Test
    @DisplayName("글 1개 조회 - 존재하지 않는 글")
    void test8() {
        // given
        Board bd = Board.builder()
                .title("foo")
                .content("bar")
                .boardImages(boardImages)
                .build();
        boardRepository.save(bd);

        // then
        assertThrows(PostNotFound.class, () -> {
            boardService.read(bd.getId() + 1L);
        });
    }

    @Test
    @DisplayName("글 삭제 - 존재하지 않는 글")
    void test9() {
        // given
        Board bd = Board.builder()
                .title("foo")
                .content("bar")
                .boardImages(boardImages)
                .build();
        boardRepository.save(bd);

        // then
        assertThrows(PostNotFound.class, () -> {
            boardService.delete(bd.getId() + 1L);
        });
    }

    @Test
    @DisplayName("글 수정 - 존재하지 않는 글")
    void test10() {
        // given
        Board bd = Board.builder()
                .title("foo")
                .content("bar")
                .boardImages(boardImages)
                .build();
        boardRepository.save(bd);

        BoardEdit boardEdit = BoardEdit.builder()
                .title(null)
                .content("빵국이 내용")
                .build();

        // then
        assertThrows(PostNotFound.class, () -> {
            boardService.edit(bd.getId() + 1L, boardEdit);
        });
    }

}