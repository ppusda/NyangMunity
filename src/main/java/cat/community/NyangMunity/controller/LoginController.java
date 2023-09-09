package cat.community.NyangMunity.controller;

import cat.community.NyangMunity.controller.form.UserForm;
import cat.community.NyangMunity.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/nm/user")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/login")
    public String loginPage(){
        return "/user/login";
    }

    @PostMapping("/login")
    private void loginComplete(@RequestBody @Valid UserForm userForm, Model model) throws IOException {
        if(loginService.userLogin(userForm.getEmail(), userForm.getPassword())){
            log.info("email : " + userForm.getEmail());
            log.info("password : " + userForm.getPassword());
        }else {
            log.info("유저 정보가 없습니다.");
        }
    }

    @RequestMapping("/kakaoLogin")
    public String loginKakao(){
        return "/main";
    }
}
