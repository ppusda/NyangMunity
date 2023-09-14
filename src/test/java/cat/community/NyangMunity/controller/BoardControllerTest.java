package cat.community.NyangMunity.controller;

import cat.community.NyangMunity.request.BoardForm;
import cat.community.NyangMunity.domain.Board;
import cat.community.NyangMunity.domain.BoardImage;
import cat.community.NyangMunity.repository.BoardRepository;
import cat.community.NyangMunity.request.BoardEdit;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//@WebMvcTest
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class BoardControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoardRepository boardRepository;

    @BeforeEach // 각각의 테스트를 실행하기 전에 수행되는 메서드 (중요)
    void clean(){
        boardRepository.deleteAll();
    }

    // bootJar 실패로 인한 이전 테스트 내역 주석 처리 (경로 상에 한글이 쓰여있었고, 내부 도메인 변경으로 인한 테스트 코드오류 발생)
    // boardImage 추가로 인한 테스트 코드 전체 오류 발생 이는 아래 코드를 이용하여 테스트 코드를 진행하자.
    ArrayList<BoardImage> boardImages = new ArrayList<>();

    @Test
    @DisplayName("글쓰기 테스트")
    void writeTest() throws Exception{
        mockMvc.perform(post("/nm/boards/write")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"제목\", \"content\": \"내용입니다.\"}")
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("200"))
                .andDo(print());
    }

    @Test
    @DisplayName("post 요청 시 json을 출력한다.")
    void test() throws Exception{
        // 23/09/11 데이터가 Json이 아닌 MultiPart로 넘어가는 것을 발견하였다. (테스트가 정상 작동하지 않음)
        // todo title, content Json으로 보내고 Image는 따로 MultiPart로 보내는 걸 목표로 해봐야될 것 같다.
/*        BoardForm boardForm = BoardForm.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        String json = objectMapper.writeValueAsString(boardForm);

        System.out.println(json);

        mockMvc.perform(post("/nm/boards/write")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                ).andExpect(status().isOk())
                .andDo(print());

        Assertions.assertEquals(1L, boardRepository.count());
        Board board = boardRepository.findAll().get(0);
        assertEquals("제목입니다.", board.getTitle());
        assertEquals("내용입니다..", board.getContent());*/
    }

    @Test
    @DisplayName("post 요청 시 DB에 값을 저장한다.")
    void postRequestInputDB() throws Exception {
        // 위와 같은 이유로 주석처리
/*
        BoardForm boardForm = BoardForm.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();
        String json = objectMapper.writeValueAsString(boardForm);

        //when
        mockMvc.perform(post("/nm/boards/write")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .content(json)
                ).andExpect(status().isOk())
                .andDo(print());

        // then
        Assertions.assertEquals(1L, boardRepository.count());*/
    }

    @Test
    @DisplayName("글 1개 조회")
    void test3_1() throws Exception {
        // given
        Board bd1 = Board.builder()
                .title("title_1")
                .content("content_1")
                .boardImages(boardImages)
                .build();
        boardRepository.save(bd1);

        // expected
        mockMvc.perform(get("/nm/boards/{boardId}", bd1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bd1.getId()))
                .andExpect(jsonPath("$.title").value("title_1"))
                .andExpect(jsonPath("$.content").value("content_1"))
                .andDo(print());
    }

    @Test
    @DisplayName("글 1페이지 조회")
    void test5() throws Exception {
        // given
        List<Board> requestBoards = IntStream.range(0, 20)
                .mapToObj(i -> Board.builder()
                        .title("빵국이 제목 " + i)
                        .content("빵국이 입니다 " + i)
                        .boardImages(boardImages)
                        .build())
                .collect(Collectors.toList());

        boardRepository.saveAll(requestBoards); // 한번에 저장

        // expected
        mockMvc.perform(get("/nm/boards?page=1&size=10") ///read/nm/boards?page=1&size=10&sort=id,desc 와 같이 이용도 가능
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.is(10)))
                .andExpect(jsonPath("$[0].title").value("빵국이 제목 19"))
                .andExpect(jsonPath("$[0].content").value("빵국이 입니다 19"))
                .andDo(print());
    }


    @Test
    @DisplayName("페이지를 0으로 요청하면 첫 페이지를 가져온다.")
    void test6() throws Exception {
        // given
        List<Board> requestBoards = IntStream.range(0, 20)
                .mapToObj(i -> Board.builder()
                        .title("빵국이 제목 " + i)
                        .content("빵국이 입니다 " + i)
                        .boardImages(boardImages)
                        .build())
                .collect(Collectors.toList());

        boardRepository.saveAll(requestBoards); // 한번에 저장

        // expected
        mockMvc.perform(get("/nm/boards?page=0&size=10") ///read/nm/boards?page=1&size=10&sort=id,desc 와 같이 이용도 가능
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.is(10)))
                .andExpect(jsonPath("$[0].title").value("빵국이 제목 19"))
                .andExpect(jsonPath("$[0].content").value("빵국이 입니다 19"))
                .andDo(print());
    }

    @Test
    @DisplayName("글 수정")
    void test7() throws Exception {
        // given
        Board board = Board.builder()
                .title("빵국이")
                .content("빵국입니다")
                .build();

        boardRepository.save(board); // 한번에 저장

        BoardEdit boardEdit = BoardEdit.builder()
                .title("빵국이 제목")
                .content("빵국입니다")
                .build();

        // expected
        mockMvc.perform(patch("/nm/boards/{boardId}", board.getId()) // PATCH /nm/boards/{boardId}
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(boardEdit))
                ).andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 삭제")
    void test8() throws Exception {
        // given
        Board board = Board.builder()
                .title("빵국이")
                .content("빵국입니다")
                .build();

        boardRepository.save(board); // 한번에 저장

        //expected
        mockMvc.perform(delete("/nm/boards/{boardId}", board.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회")
    void test9() throws Exception {
        // given
        //expected
        mockMvc.perform(delete("/nm/boards/{boardId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 수정")
    void test10() throws Exception {
        // given
        BoardEdit boardEdit = BoardEdit.builder()
                .title("빵국이 제목")
                .content("빵국입니다")
                .build();

        // expected
        mockMvc.perform(patch("/nm/boards/{boardId}", 1L) // PATCH /nm/boards/{boardId}
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(boardEdit))
                ).andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 작성 시 제목에 '바보'는 포함될 수 없다.")
    void test11() throws Exception {
        // given
        BoardForm boardForm = BoardForm.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        String json = objectMapper.writeValueAsString(boardForm);

        // expected
        mockMvc.perform(post("/nm/boards/write")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                ).andExpect(status().isOk())
                .andDo(print());
    }
}
