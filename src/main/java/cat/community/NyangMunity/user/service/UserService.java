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
import cat.community.NyangMunity.user.request.UserEditForm;
import cat.community.NyangMunity.user.request.UserJoinForm;
import cat.community.NyangMunity.user.request.UserLoginForm;
import cat.community.NyangMunity.user.response.UserInfos;
import cat.community.NyangMunity.user.response.UserTokens;
import cat.community.NyangMunity.user.response.UserLoginResponse;
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
    public UserLoginResponse userLogin(UserLoginForm userLoginForm) {
        User user = getUserByEmail(userLoginForm.email())
                .orElseThrow(InvalidLoginInformationException::new);

        if(!isPasswordMatches(userLoginForm.password(), user)) {
            throw new InvalidLoginInformationException();
        }

        return UserLoginResponse.builder()
                .userInfos(UserInfos.from(user.getId(), user.getNickname()))
                .userTokens(createTokens(user.getId()))
                .build();
    }

    private UserTokens createTokens(Long userId) {
        String accessToken = jwtTokenProvider.createAccessToken(userId);
        String refreshToken = jwtTokenProvider.createRefreshToken(userId);
        tokenService.register(refreshToken, userId);

        return UserTokens.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public void register(UserJoinForm userJoinForm) {
        checkDuplicateEmail(userJoinForm.email());
        checkDuplicateNickname(userJoinForm.nickname());

        User user = User.builder()
                .email(userJoinForm.email())
                .password(scryptPasswordEncoder.encrypt(userJoinForm.password()))
                .nickname(userJoinForm.nickname())
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
    public void userEdit(UserEditForm userEditForm, Long userId) {
        User user = getUserById(userId);

        if(!isPasswordMatches(userEditForm.password(), user)) {
            throw new InvalidPasswordException();
        }

        UserEditor.UserEditorBuilder userEditorBuilder = user.toEditor();

        UserEditor userEditor = userEditorBuilder
                .nickname(userEditForm.nickname() != null && !userEditForm.nickname().isEmpty()
                        ? userEditForm.nickname() : user.getNickname())
                .password(userEditForm.password() != null && !userEditForm.password().isEmpty()
                        ? scryptPasswordEncoder.encrypt(userEditForm.password()) : user.getPassword())
                .birthday(userEditForm.birthday() != null && !userEditForm.birthday().isEmpty() && !userEditForm.birthday().equals("null")
                        ? LocalDate.parse(userEditForm.birthday(), DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null)
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

