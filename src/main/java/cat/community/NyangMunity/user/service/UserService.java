package cat.community.NyangMunity.user.service;

import cat.community.NyangMunity.global.exception.UserNotFoundException;
import cat.community.NyangMunity.global.provider.KakaoAuthProvider;
import cat.community.NyangMunity.global.crypto.ScryptPasswordEncoder;
import cat.community.NyangMunity.user.entity.User;
import cat.community.NyangMunity.user.editor.UserEditor;
import cat.community.NyangMunity.global.exception.AlreadyExistsEmailException;
import cat.community.NyangMunity.global.exception.InvalidRequest;
import cat.community.NyangMunity.global.exception.InvalidSigninInformation;
import cat.community.NyangMunity.global.exception.UnauthorizedUserException;
import cat.community.NyangMunity.token.repository.TokenRepository;
import cat.community.NyangMunity.user.repository.UserRepository;
import cat.community.NyangMunity.user.request.UserForm;
import cat.community.NyangMunity.user.response.KakaoTokenResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final KakaoAuthProvider kakaoAuthProvider;
    private final ScryptPasswordEncoder scryptPasswordEncoder;

    public User getUserById(Long uid) {
        return userRepository.findById(uid).orElseThrow(UserNotFoundException::new);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(InvalidSigninInformation::new);
    }

    @Transactional
    public Long userLogin(UserForm userForm) {
        User user = getUserByEmail(userForm.getEmail());

        if(!scryptPasswordEncoder.matches(userForm.getPassword(), user.getPassword())) {
            throw new InvalidSigninInformation();
        }

        return user.getId();
    }

    public void register(UserForm userForm) {
        Optional<User> userCheck = userRepository.findByEmail(userForm.getEmail());
        if(userCheck.isPresent()){
            throw new AlreadyExistsEmailException();
        } else {
            User user = User.builder()
                    .email(userForm.getEmail())
                    .password(scryptPasswordEncoder.encrypt(userForm.getPassword()))
                    .nickname(userForm.getNickname())
                    .birthday(userForm.getBirthday() != null && !userForm.getBirthday().isEmpty()
                            ? LocalDate.parse(userForm.getBirthday(), DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null)
                    .createDate(LocalDateTime.now())
                    .build();
            userRepository.save(user);
        }
    }

    public boolean userCheck(String nowPassword, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UnauthorizedUserException::new);

        return scryptPasswordEncoder.matches(nowPassword, user.getPassword());
    }

    @Transactional
    public void userLogout(Long userId) {
        tokenRepository.deleteByUserId(userId);
    }

    public User userInfo(Long userId) {
        if(!userId.equals(0L)){
            return getUserById(userId);
        } else{
            throw new InvalidRequest();
        }
    }

    @Transactional
    public void userEdit(UserForm userForm, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UnauthorizedUserException::new);

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

    public void userCancel(Long userId) {
        if(!userId.equals(0L)){
            userRepository.deleteById(userId);
        } else{
            throw new InvalidRequest();
        }
    }

    public void kakaoLogin(String code) {
        KakaoTokenResponse tokenResponse = kakaoAuthProvider.getToken(code);
        log.info(tokenResponse.getAccess_token());
        log.info(tokenResponse.getRefresh_token());

        log.info(kakaoAuthProvider.getUserInfo(tokenResponse.getAccess_token()).getKakaoAccount().toString());
    }
}

