package cat.community.NyangMunity.service;

import cat.community.NyangMunity.controller.form.BoardForm;
import cat.community.NyangMunity.domain.Board;
import cat.community.NyangMunity.repository.BoardRepository;
import cat.community.NyangMunity.response.BoardResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    @DisplayName("글 작성")
    void test1(){
        // given
        BoardForm boardForm = BoardForm.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        // when
        boardService.write(boardForm);

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
                .build();
        boardRepository.save(bd);

        // when
        BoardResponse response = boardService.read(bd.getId());

        // then
        assertNotNull(response);
        assertEquals(1L, boardRepository.count());
        assertEquals("foo", response.getTitle());
        assertEquals("bar", response.getContent());
    }
}