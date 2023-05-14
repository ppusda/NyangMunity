package cat.community.NyangMunity.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;

@Controller
@RequiredArgsConstructor
public class MainController {

    @GetMapping("/")
    public String main() {
        return "docs/apiInfo";
    }
}
