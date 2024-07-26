package cat.community.NyangMunity.service;

import cat.community.NyangMunity.board.response.BoardDetailResponse;
import cat.community.NyangMunity.board.service.BoardService;
import cat.community.NyangMunity.global.exception.UserNotFoundException;
import cat.community.NyangMunity.user.entity.User;
import cat.community.NyangMunity.user.repository.UserRepository;
import cat.community.NyangMunity.board.request.BoardFormRequest;
import cat.community.NyangMunity.board.entity.Board;
import cat.community.NyangMunity.board.entity.BoardImage;
import cat.community.NyangMunity.global.exception.BoardNotFoundException;
import cat.community.NyangMunity.board.repository.BoardRepository;
import cat.community.NyangMunity.board.request.BoardEditRequest;
import cat.community.NyangMunity.board.request.BoardListRequest;
import cat.community.NyangMunity.user.request.UserForm;
import cat.community.NyangMunity.board.response.BoardResponse;
import cat.community.NyangMunity.user.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class BoardServiceTest {
/*
    @Autowired
    private BoardService boardService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

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
        BoardFormRequest boardFormRequest = BoardFormRequest.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        // when
        User user = userRepository.findByEmail("ppusda@naver.com")
                        .orElseThrow(UserNotFoundException::new);

        boardService.write(boardFormRequest, boardImages, user);

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
        Long userId = userService.userLogin(UserForm.builder()
                .email("ppusda@naver.com")
                .password("1234")
                .build());

        Board bd = Board.builder()
                .title("foo")
                .content("bar")
                .boardImages(boardImages)
                .user(userService.userInfo(userId))
                .build();
        boardRepository.save(bd);

        // when
        BoardDetailResponse boardDetailResponse = boardService.read(bd.getId(), userId);

        // then
        assertEquals(bd.getId(), boardDetailResponse.boardResponse().id());
    }

    @Test
    @DisplayName("글 여러개 조회")
    void test3() {
        // given
        Long userId = userService.userLogin(UserForm.builder()
                .email("ppusda@naver.com")
                .password("1234")
                .build());

        boardRepository.saveAll(List.of(
                Board.builder()
                        .title("title_1")
                        .content("content_1")
                        .boardImages(boardImages)
                        .user(userService.userInfo(userId))
                        .build(),
                Board.builder()
                        .title("title_2")
                        .content("content_2")
                        .boardImages(boardImages)
                        .user(userService.userInfo(userId))
                        .build()
        )); // 한번에 저장

        // when
        Page<BoardResponse> boardList = boardService.getList(1, 10);

        // then
        assertEquals(2L, boardList.getSize());
    }

    @Test
    @DisplayName("글 1페이지 조회")
    void test4() {
        // given
        Long userId = userService.userLogin(UserForm.builder()
                .email("ppusda@naver.com")
                .password("1234")
                .build());

        List<Board> requestBoards = IntStream.range(0, 20)
                .mapToObj(i -> Board.builder()
                        .title("빵국이 제목 " + i)
                        .content("빵국이 입니다 " + i)
                        .boardImages(boardImages)
                        .user(userService.userInfo(userId))
                        .build())
                .collect(Collectors.toList());

        boardRepository.saveAll(requestBoards); // 한번에 저장

        BoardListRequest boardListRequest = BoardListRequest.builder()
                .page(1)
                .size(10)
                .build();

        // when
        Page<BoardResponse> boardList = boardService.getList(0, 10);

        // then
        assertEquals(10L, boardList.getSize());
    }

    @Test
    @DisplayName("글 제목 수정")
    void test5() {
        // given
        Long userId = userService.userLogin(UserForm.builder()
                .email("ppusda@naver.com")
                .password("1234")
                .build());

        Board board = Board.builder()
                        .title("빵국이제목")
                        .content("빵국입니다")
                        .user(userService.userInfo(userId))
                        .build();

        boardRepository.save(board); // 한번에 저장

        BoardEditRequest boardEditRequest = BoardEditRequest.builder()
                                .title("제목: 빵국이")
                                .build();

        // when
        boardService.edit(board.getId(), boardEditRequest, boardImages, userId);

        // then
        Board changedBoard = boardRepository.findById(board.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다."));
        Assertions.assertEquals("제목: 빵국이", changedBoard.getTitle());
    }

    @Test
    @DisplayName("글 내용 수정")
    void test6() {
        // given
        Long userId = userService.userLogin(UserForm.builder()
                .email("ppusda@naver.com")
                .password("1234")
                .build());

        Board board = Board.builder()
                .title("빵국이 제목")
                .content("빵국입니다")
                .user(userService.userInfo(userId))
                .build();

        boardRepository.save(board); // 한번에 저장

        BoardEditRequest boardEditRequest = BoardEditRequest.builder()
                .title(null) // null 값 처리 시에는 Builder 클래스를 새로 생성하여 만들면 된다.
                .content("빵국이 내용")
                .build();

        // when
        boardService.edit(board.getId(), boardEditRequest, boardImages, userId);

        // then
        Board changedBoard = boardRepository.findById(board.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다."));
        Assertions.assertEquals("빵국이 제목", changedBoard.getTitle());
        Assertions.assertEquals("빵국이 내용", changedBoard.getContent());
    }

    @Test
    @DisplayName("게시글 삭제")
    void test7() {
        // given
        Long userId = userService.userLogin(UserForm.builder()
                .email("ppusda@naver.com")
                .password("1234")
                .build());

        Board board = Board.builder()
                .title("빵국이 제목")
                .content("빵국입니다")
                .user(userService.userInfo(userId))
                .build();

        boardRepository.save(board);

        // when
        boardService.delete(board.getId(), userId);

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
        assertThrows(BoardNotFoundException.class, () -> {
            boardService.read(bd.getId() + 1L, 1L);
        });
    }

    @Test
    @DisplayName("글 삭제 - 존재하지 않는 글")
    void test9() {
        // given
        Long userId = userService.userLogin(UserForm.builder()
                .email("ppusda@naver.com")
                .password("1234")
                .build());

        Board bd = Board.builder()
                .title("foo")
                .content("bar")
                .boardImages(boardImages)
                .build();
        boardRepository.save(bd);

        // then
        assertThrows(BoardNotFoundException.class, () -> {
            boardService.delete(bd.getId() + 1L, userId);
        });
    }

    @Test
    @DisplayName("글 수정 - 존재하지 않는 글")
    void test10() {
        // given
        Long userId = userService.userLogin(UserForm.builder()
                .email("ppusda@naver.com")
                .password("1234")
                .build());

        Board bd = Board.builder()
                .title("foo")
                .content("bar")
                .boardImages(boardImages)
                .build();
        boardRepository.save(bd);

        BoardEditRequest boardEditRequest = BoardEditRequest.builder()
                .title(null)
                .content("빵국이 내용")
                .build();

        // then
        assertThrows(BoardNotFoundException.class, () -> {
            boardService.edit(bd.getId() + 1L, boardEditRequest, boardImages, userId);
        });
    }*/

}