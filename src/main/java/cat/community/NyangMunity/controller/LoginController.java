package cat.community.NyangMunity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller

public class LoginController {

    @RequestMapping("/login")
    public String loginPage(){

        return "/login/login";
    }

    @RequestMapping("/kakaoLogin")
    public String kakaoLogin(){

        return "/main";
    }
}
