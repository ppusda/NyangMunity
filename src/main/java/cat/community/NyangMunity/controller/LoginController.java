package cat.community.NyangMunity.controller;

import cat.community.NyangMunity.controller.form.UserForm;
import cat.community.NyangMunity.domain.User;
import cat.community.NyangMunity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class LoginController {
    private final UserRepository userRepository;

    @GetMapping("/login")
    public String loginPage(){
        return "/user/login";
    }

    @PostMapping("/login")
    private String loginComplete(HttpServletResponse response, UserForm userForm, Model model) throws IOException {
        User loginUser;
        try{
            loginUser = userRepository.findByEmailPassword(userForm.getEmail(), userForm.getPassword());
            model.addAttribute("loginState", "true");
        }catch (Exception e){
            model.addAttribute("loginState", "fail");
            return "/user/login";
        }

        model.addAttribute("loginUser", loginUser);

        return "/community/community";
    }

    @RequestMapping("/kakaoLogin")
    public String loginKakao(){

        return "/main";
    }
}
