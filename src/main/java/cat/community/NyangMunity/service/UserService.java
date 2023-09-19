package cat.community.NyangMunity.service;

import cat.community.NyangMunity.domain.Session;
import cat.community.NyangMunity.domain.User;
import cat.community.NyangMunity.exception.InvalidSigninInformation;
import cat.community.NyangMunity.repository.SessionRepository;
import cat.community.NyangMunity.repository.UserRepository;
import cat.community.NyangMunity.request.UserForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public List<User> findAll(){
        return userRepository.findAll();
    }

    @Transactional
    public String userLogin(UserForm userForm) {
        User user = userRepository.findByEmailAndPassword(userForm.getEmail(), userForm.getPassword())
                .orElseThrow(InvalidSigninInformation::new);
        return user.addSession().getAccessToken();
    }

    public void register(UserForm userForm) {
        User user = User.builder()
                .email(userForm.getEmail())
                .password(userForm.getPassword())
                .nickname(userForm.getNickname())
                .birthday(LocalDate.parse(userForm.getBirthday(), DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .createDate(LocalDateTime.now())
                .build();

        userRepository.save(user);
    }

    public String userCheck(String SID) {
        Optional<Session> session = sessionRepository.findByAccessToken(SID);
        return session.get().getUser().getNickname();
    }

    @Transactional
    public void userLogout(String SID) {
        sessionRepository.deleteByAccessToken(SID);
    }
}

