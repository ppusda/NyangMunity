package cat.community.NyangMunity.service;

import cat.community.NyangMunity.domain.User;
import cat.community.NyangMunity.repository.LoginRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {
    private final LoginRepository loginRepository;

    public List<User> findAll(){
        return loginRepository.findAll();
    }

    public boolean userLogin(String email, String password) {
        User user = loginRepository.findByEmailPassword(email, password);
        if (user != null){
            return true;
        }else {
            return false;
        }

    }
}

