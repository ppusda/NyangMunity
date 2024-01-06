package cat.community.NyangMunity.service;

import cat.community.NyangMunity.global.crypto.ScryptPasswordEncoder;
import cat.community.NyangMunity.user.entity.User;
import cat.community.NyangMunity.global.exception.AlreadyExistsEmailException;
import cat.community.NyangMunity.global.exception.InvalidSigninInformation;
import cat.community.NyangMunity.user.repository.UserRepository;
import cat.community.NyangMunity.user.request.UserForm;
import cat.community.NyangMunity.user.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ScryptPasswordEncoder scryptPasswordEncoder;

    @BeforeEach //@AfterEach
    void clean() { userRepository.deleteAll(); }

    @Test
    @DisplayName("회원가입 성공")
    void registerTest() {
        // given
        UserForm userForm = UserForm.builder()
                .email("ppusda@naver.com")
                .password("1234")
                .nickname("빵")
                .build();

        // when
        userService.register(userForm);

        // then
        Assertions.assertEquals(1, userRepository.count());

        User user = userRepository.findAll().iterator().next();
        assertEquals("ppusda@naver.com", user.getEmail());
        assertTrue(scryptPasswordEncoder.matches("1234", user.getPassword()));
        assertEquals("빵", user.getNickname());
    }

    @Test
    @DisplayName("회원가입 시 중복된 이메일")
    void registerDuplicateEmail() {
        // given
        UserForm userForm = UserForm.builder()
                .email("ppusda@naver.com")
                .password("1234")
                .nickname("빵")
                .build();
        userService.register(userForm);

        // expected
        assertThrows(AlreadyExistsEmailException.class, () -> userService.register(userForm));

    }

    @Test
    @DisplayName("로그인 성공")
    void loginSuccess() {
        // given
        User registerForm = User.builder()
                .email("ppusda@naver.com")
                .password(scryptPasswordEncoder.encrypt("1234"))
                .nickname("빵")
                .createDate(LocalDateTime.now())
                .build();

        userRepository.save(registerForm);

        UserForm loginForm = UserForm.builder()
                .email("ppusda@naver.com")
                .password("1234")
                .build();

        // when
        Long userId = userService.userLogin(loginForm);

        // then
        assertNotNull(userId);
    }

    @Test
    @DisplayName("로그인 시 비밀번호 틀림")
    void loginFail() {
        // given
        UserForm registerForm = UserForm.builder()
                .email("ppusda@naver.com")
                .password("1234")
                .nickname("빵")
                .build();
        userService.register(registerForm);

        UserForm loginForm = UserForm.builder()
                .email("ppusda@naver.com")
                .password("5678")
                .build();

        // when
        assertThrows(InvalidSigninInformation.class, () -> userService.userLogin(loginForm));

    }
}