package cat.community.NyangMunity.controller;

import cat.community.NyangMunity.domain.Session;
import cat.community.NyangMunity.domain.User;
import cat.community.NyangMunity.repository.SessionRepository;
import cat.community.NyangMunity.repository.UserRepository;
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
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest
@AutoConfigureMockMvc
@SpringBootTest
class LoginControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @BeforeEach
        // 각각의 테스트를 실행하기 전에 수행되는 메서드 (중요)
    void clean(){
        userRepository.deleteAll();
        sessionRepository.deleteAll();
    }

    // bootJar 실패로 인한 이전 테스트 내역 주석 처리 (23/09/13 해결)
    @Test
    @DisplayName("로그인 성공")
    void loginTest() throws Exception{
        // given
        userRepository.save(User.builder()
                .email("ppusda@naver.com")
                .password("1234")
                .nickname("국")
                .createDate(LocalDateTime.now())
                .build());

        User user = User.builder()
                .email("ppusda@naver.com")
                .password("1234") // Scrypt, Bcrypt
                .build();

        String json = objectMapper.writeValueAsString(user);

        // expected
        mockMvc.perform(post("/nm/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                ).andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Transactional
    @DisplayName("로그인 성공 후 세션 응답")
    void sessionTest() throws Exception{
        // given
        User user = User.builder()
                .email("ppusda@naver.com")
                .password("1234")
                .nickname("국")
                .createDate(LocalDateTime.now())
                .build();

        userRepository.save(user);

        String json = objectMapper.writeValueAsString(user);

        // expected
        mockMvc.perform(post("/nm/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(Matchers.notNullValue()))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 후 권한이 필요한 페이지에 접속한다.")
    void accessPermissionPage() throws Exception{
        // given
        User user = User.builder()
                .email("ppusda@naver.com")
                .password("1234")
                .nickname("국")
                .createDate(LocalDateTime.now())
                .build();

        Session session = user.addSession();
        userRepository.save(user);

        // expected
        mockMvc.perform(get("/nm/test")
                        .header("Authorization", session.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("검증되지 않은 세션 값으로 권한이 필요한 페이지에 접근 할 수 없다.")
    void CantAccessPermissionPage() throws Exception{
        // given
        User user = User.builder()
                .email("ppusda@naver.com")
                .password("1234")
                .nickname("국")
                .createDate(LocalDateTime.now())
                .build();

        Session session = user.addSession();
        userRepository.save(user);

        // expected
        mockMvc.perform(get("/nm/test")
                        .header("Authorization", session.getAccessToken() + "-other")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isUnauthorized())
                .andDo(print());
    }


}