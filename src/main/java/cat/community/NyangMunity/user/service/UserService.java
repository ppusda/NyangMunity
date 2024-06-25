package cat.community.NyangMunity.user.service;

import cat.community.NyangMunity.global.exception.AlreadyExistsNicknameException;
import cat.community.NyangMunity.global.exception.InvalidPasswordException;
import cat.community.NyangMunity.global.exception.UserNotFoundException;
import cat.community.NyangMunity.global.provider.JwtTokenProvider;
import cat.community.NyangMunity.global.crypto.ScryptPasswordEncoder;
import cat.community.NyangMunity.token.service.TokenService;
import cat.community.NyangMunity.user.entity.User;
import cat.community.NyangMunity.user.editor.UserEditor;
import cat.community.NyangMunity.global.exception.AlreadyExistsEmailException;
import cat.community.NyangMunity.global.exception.InvalidLoginInformationException;
import cat.community.NyangMunity.user.repository.UserRepository;
import cat.community.NyangMunity.user.request.UserForm;
import cat.community.NyangMunity.user.response.UserResponse;
import cat.community.NyangMunity.user.response.UserTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ScryptPasswordEncoder scryptPasswordEncoder;

    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public UserTokenResponse userLogin(UserForm userForm) {
        User user = getUserByEmail(userForm.getEmail())
                .orElseThrow(InvalidLoginInformationException::new);

        if(!isPasswordMatches(userForm.getPassword(), user)) {
            throw new InvalidLoginInformationException();
        }

        return createTokens(user.getId());
    }

    private UserTokenResponse createTokens(Long userId) {
        String accessToken = jwtTokenProvider.createAccessToken(userId);
        String refreshToken = jwtTokenProvider.createRefreshToken(userId);
        tokenService.register(refreshToken, userId);

        return UserTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public void register(UserForm userForm) {
        checkDuplicateEmail(userForm.getEmail());
        checkDuplicateNickname(userForm.getNickname());

        User user = User.builder()
                .email(userForm.getEmail())
                .password(scryptPasswordEncoder.encrypt(userForm.getPassword()))
                .nickname(userForm.getNickname())
                .createDate(LocalDateTime.now())
                .build();

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public void checkDuplicateEmail(String email) {
        getUserByEmail(email).orElseThrow(AlreadyExistsEmailException::new);
    }

    @Transactional(readOnly = true)
    public void checkDuplicateNickname(String nickname) {
        userRepository.findByNickname(nickname).orElseThrow(AlreadyExistsNicknameException::new);
    }

    @Transactional
    public void userLogout(Long userId) {
        tokenService.deleteToken(userId);
    }

    @Transactional
    public void userEdit(UserForm userForm, Long userId) {
        User user = getUserById(userId);

        if(!isPasswordMatches(userForm.getPassword(), user)) {
            throw new InvalidPasswordException();
        }

        UserEditor.UserEditorBuilder userEditorBuilder = user.toEditor();

        UserEditor userEditor = userEditorBuilder
                .nickname(userForm.getNickname() != null && !userForm.getNickname().isEmpty()
                        ? userForm.getNickname() : user.getNickname())
                .password(userForm.getPassword() != null && !userForm.getPassword().isEmpty()
                        ? scryptPasswordEncoder.encrypt(userForm.getPassword()) : user.getPassword())
                .birthday(userForm.getBirthday() != null && !userForm.getBirthday().isEmpty() && !userForm.getBirthday().equals("null")
                        ? LocalDate.parse(userForm.getBirthday(), DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null)
                .build();

        user.edit(userEditor);
    }

    @Transactional
    public void userCancel(Long userId) {
        userRepository.deleteById(userId);
    }

    private boolean isPasswordMatches(String password, User user) {
        return scryptPasswordEncoder.matches(password, user.getPassword());
    }
}

