package cat.community.NyangMunity.service;

import cat.community.NyangMunity.config.JwtTokenProvider;;
import cat.community.NyangMunity.crypto.ScryptPasswordEncoder;
import cat.community.NyangMunity.domain.User;
import cat.community.NyangMunity.domain.UserEditor;
import cat.community.NyangMunity.exception.AlreadyExistsEmailException;
import cat.community.NyangMunity.exception.InvalidRequest;
import cat.community.NyangMunity.exception.InvalidSigninInformation;
import cat.community.NyangMunity.exception.Unauthorized;
import cat.community.NyangMunity.repository.TokenRepository;
import cat.community.NyangMunity.repository.UserRepository;
import cat.community.NyangMunity.request.UserForm;
import cat.community.NyangMunity.service.util.TokenRefresher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
    private final TokenRefresher tokenRefresher;
    private final ScryptPasswordEncoder scryptPasswordEncoder;

    @Transactional
    public Long userLogin(UserForm userForm) {
        User user = userRepository.findByEmail(userForm.getEmail())
                .orElseThrow(InvalidSigninInformation::new);

        if(!scryptPasswordEncoder.matches(userForm.getPassword(), user.getPassword())) {
            throw new InvalidSigninInformation();
        }

        if(!user.getTokens().isEmpty()) {
            tokenRefresher.removeRefreshToken(user);
        }

        tokenRefresher.addRefreshToken(user);

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
                .orElseThrow(Unauthorized::new);

        return scryptPasswordEncoder.matches(nowPassword, user.getPassword());
    }

    @Transactional
    public void userLogout(Long userId) {
        tokenRepository.deleteByUserId(userId);
    }

    public User userInfo(Long userId) {
        if(!userId.equals(0L)){
            User user = userRepository.findById(userId)
                    .orElseThrow(Unauthorized::new);
            return user;
        } else{
            throw new InvalidRequest();
        }
    }

    @Transactional
    public void userEdit(UserForm userForm, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(Unauthorized::new);

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
}

