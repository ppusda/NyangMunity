package cat.community.NyangMunity.service;

import cat.community.NyangMunity.domain.Session;
import cat.community.NyangMunity.domain.User;
import cat.community.NyangMunity.exception.InvalidSigninInformation;
import cat.community.NyangMunity.repository.UserRepository;
import cat.community.NyangMunity.request.UserForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;

    public List<User> findAll(){
        return userRepository.findAll();
    }

    @Transactional
    public String userLogin(UserForm userForm) {
        User user = userRepository.findByEmailAndPassword(userForm.getEmail(), userForm.getPassword())
                .orElseThrow(InvalidSigninInformation::new);
        return user.addSession().getAccessToken();
    }
}

