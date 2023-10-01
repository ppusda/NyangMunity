package cat.community.NyangMunity.service;

import cat.community.NyangMunity.domain.User;
import cat.community.NyangMunity.exception.AlreadyExistsEmailException;
import cat.community.NyangMunity.repository.UserRepository;
import cat.community.NyangMunity.request.UserForm;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @AfterEach //@BeforeEach
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
        assertEquals("ppusda", user.getEmail());
        assertEquals("1234", user.getPassword());
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

        // expected
        assertThrows(AlreadyExistsEmailException.class, () -> userService.register(userForm));

    }
}