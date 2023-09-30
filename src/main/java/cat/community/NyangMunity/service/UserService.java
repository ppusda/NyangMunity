package cat.community.NyangMunity.service;

import cat.community.NyangMunity.config.AppConfig;
import cat.community.NyangMunity.config.JwtTokenProvider;
import cat.community.NyangMunity.domain.Token;
import cat.community.NyangMunity.domain.User;
import cat.community.NyangMunity.exception.InvalidSigninInformation;
import cat.community.NyangMunity.repository.TokenRepository;
import cat.community.NyangMunity.repository.UserRepository;
import cat.community.NyangMunity.request.UserForm;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public List<User> findAll(){
        return userRepository.findAll();
    }

    public Long userLogin(UserForm userForm) {
        User user = userRepository.findByEmailAndPassword(userForm.getEmail(), userForm.getPassword())
                .orElseThrow(InvalidSigninInformation::new);

        if(!user.getTokens().isEmpty()) {
            tokenRepository.deleteByUserId(user.getId());
        }

        user.addToken(jwtTokenProvider.createRefreshToken(user.getId()));

        return user.getId();
    }

    public void register(UserForm userForm) {
        User user = User.builder()
                .email(userForm.getEmail())
                .password(userForm.getPassword())
                .nickname(userForm.getNickname())
                .birthday(userForm.getBirthday() != null && !userForm.getBirthday().isEmpty()
                        ? LocalDate.parse(userForm.getBirthday(), DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null)
                .createDate(LocalDateTime.now())
                .build();

        userRepository.save(user);
    }

    public User userCheck(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.get();
    }

    public void userLogout(Long userId) {
        tokenRepository.deleteByUserId(userId);
    }
}

