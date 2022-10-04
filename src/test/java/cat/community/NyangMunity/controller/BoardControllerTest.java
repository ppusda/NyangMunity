package cat.community.NyangMunity.controller;

import cat.community.NyangMunity.repository.BoardRepository;
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

import javax.transaction.Transactional;

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
    @DisplayName("미로미로 빔")
    void test3() throws Exception {
        // when
        mockMvc.perform(post("/write")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"miro babo\", \"content\": \"go to baboo channel\"}")
                ).andExpect(status().isOk())
                .andDo(print());

        // then
        Assertions.assertEquals(1L, boardRepository.count());
    }

}

