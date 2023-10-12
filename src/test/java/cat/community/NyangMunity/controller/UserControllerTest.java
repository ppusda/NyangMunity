package cat.community.NyangMunity.controller;

import cat.community.NyangMunity.config.JwtTokenProvider;
import cat.community.NyangMunity.crypto.ScryptPasswordEncoder;
import cat.community.NyangMunity.domain.Token;
import cat.community.NyangMunity.domain.User;
import cat.community.NyangMunity.repository.TokenRepository;
import cat.community.NyangMunity.repository.UserRepository;
import cat.community.NyangMunity.request.UserForm;
import cat.community.NyangMunity.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest
@AutoConfigureMockMvc
@SpringBootTest
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ScryptPasswordEncoder scryptPasswordEncoder;

    @BeforeEach
        // 각각의 테스트를 실행하기 전에 수행되는 메서드 (중요)
    void clean(){
        userRepository.deleteAll();
        tokenRepository.deleteAll();
    }

    // bootJar 실패로 인한 이전 테스트 내역 주석 처리 (23/09/13 해결)
    @Test
    @DisplayName("회원가입 성공")
    void registerTest() throws Exception{
        // given
        UserForm userForm = UserForm.builder()
                .email("ppusda@naver.com")
                .password("1234") // Scrypt, Bcrypt
                .nickname("빵")
                .build();

        String json = objectMapper.writeValueAsString(userForm);

        // expected
        mockMvc.perform(post("/nm/user/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                ).andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 성공")
    void loginTest() throws Exception{
        // given
        userRepository.save(User.builder()
                .email("ppusda@naver.com")
                .password(scryptPasswordEncoder.encrypt("1234"))
                .nickname("국")
                .createDate(LocalDateTime.now())
                .build());

        UserForm userform = UserForm.builder()
                .email("ppusda@naver.com")
                .password("1234") // Scrypt, Bcrypt
                .build();

        String json = objectMapper.writeValueAsString(userform);

        // expected
        mockMvc.perform(post("/nm/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                ).andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Transactional
    @DisplayName("로그인 후 권한이 필요한 페이지에 접속한다.")
    void accessPermissionPage() throws Exception{
        // given
        userService.register(UserForm.builder()
                .email("ppusda@naver.com")
                .password("1234")
                .nickname("빵")
                .build());

        Long uid = userService.userLogin(UserForm.builder()
                .email("ppusda@naver.com")
                .password("1234")
                .nickname("빵")
                .build());

        String token = jwtTokenProvider.createAccessToken(uid);

        // expected
        mockMvc.perform(post("/nm/user/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("SESSION", token))
                        .content("{SID:"+token+"}")
                ).andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Transactional
    @DisplayName("검증되지 않은 세션 값으로 권한이 필요한 페이지에 접근 할 수 없다.")
    void CantAccessPermissionPage() throws Exception{
        // given
        userService.register(UserForm.builder()
                .email("ppusda@naver.com")
                .password("1234")
                .nickname("빵")
                .build());

        Long uid = userService.userLogin(UserForm.builder()
                .email("ppusda@naver.com")
                .password("1234")
                .nickname("빵")
                .build());

        List<Token> tokens = tokenRepository.findByUserId(uid);

        // expected
        mockMvc.perform(post("/nm/user/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("SESSION", tokens.get(0).getRefreshToken() + "-other"))
                        .content("{SID:"+tokens.get(0).getRefreshToken() + "-other"+"}")
                ).andExpect(status().isUnauthorized())
                .andDo(print());
    }


}