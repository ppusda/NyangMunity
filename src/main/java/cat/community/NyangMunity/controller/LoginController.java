package cat.community.NyangMunity.controller;

import cat.community.NyangMunity.controller.form.UserForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class LoginController {

    @GetMapping("/login")
    public String loginPage(){
        return "/user/login";
    }

    @PostMapping("/login")
    private void loginComplete(@RequestBody @Valid UserForm userForm, Model model) throws IOException {
        log.info("email : " + userForm.getEmail());
        log.info("password : " + userForm.getPassword());
    }

    @RequestMapping("/kakaoLogin")
    public String loginKakao(){
        return "/main";
    }
}
