package cat.community.NyangMunity.controller;

import cat.community.NyangMunity.controller.form.BoardForm;
import cat.community.NyangMunity.domain.Board;
import cat.community.NyangMunity.repository.BoardRepository;
import cat.community.NyangMunity.response.BoardResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Test
    @DisplayName("글쓰기 테스트")
    void writeTest() throws Exception{
        mockMvc.perform(post("/write")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": null, \"content\": \"go to baboo channel\"}")
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.title").value("제목을 입력해주세요."))
                .andDo(print());
    }

    @Test
    @DisplayName("post 요청 시 json을 출력한다.")
    void test() throws Exception{

        BoardForm boardForm = BoardForm.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        String json = objectMapper.writeValueAsString(boardForm);

        System.out.println(json);

        mockMvc.perform(post("/write")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                ).andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("post 요청 시 DB에 값을 저장한다.")
    void postRequestInputDB() throws Exception {

        BoardForm boardForm = BoardForm.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        String json = objectMapper.writeValueAsString(boardForm);

        // when
        mockMvc.perform(post("/write")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                ).andExpect(status().isOk())
                .andDo(print());

        // then
        Assertions.assertEquals(1L, boardRepository.count());
    }

    @Test
    @DisplayName("글 여러개 조회")
    void test4() throws Exception {
        // given
        Board bd1 = Board.builder()
                .title("title_1")
                .content("content_1")
                .build();
        boardRepository.save(bd1);

        Board bd2 = Board.builder()
                .title("title_2")
                .content("content_2")
                .build();
        boardRepository.save(bd2);

        // expected
        mockMvc.perform(get("/read/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.is(2)))
                .andExpect(jsonPath("$.[0].id").value(bd1.getId()))
                .andExpect(jsonPath("$.[0].title").value("title_1"))
                .andExpect(jsonPath("$.[0].content").value("content_1"))
                .andExpect(jsonPath("$.[1].id").value(bd2.getId()))
                .andExpect(jsonPath("$.[1].title").value("title_2"))
                .andExpect(jsonPath("$.[1].content").value("content_2"))
                .andDo(print());
    }

    @Test
    @DisplayName("글 1페이지 조회")
    void test5() throws Exception {
        // given
        List<Board> requestBoards = IntStream.range(1, 31)
                .mapToObj(i -> Board.builder()
                        .title("빵국이 제목 " + i)
                        .content("빵국이 입니다 " + i)
                        .build())
                .collect(Collectors.toList());

        boardRepository.saveAll(requestBoards); // 한번에 저장

        // expected
        mockMvc.perform(get("/read/boards?page=1&sort=id,desc")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.is(5)))
                //.andExpect(jsonPath("$.id()").value(30))
                .andExpect(jsonPath("$.title()").value("빵국이 제목 30"))
                .andExpect(jsonPath("$.content()").value("빵국이 입니다 30"))
                .andDo(print());
    }
}

