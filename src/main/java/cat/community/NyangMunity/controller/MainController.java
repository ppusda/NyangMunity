package cat.community.NyangMunity.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

@Controller
@RequiredArgsConstructor
public class MainController {

    @GetMapping("/api")
    public String main() {
        return "docs/apiInfo";
    }
    @RequestMapping(value = "/{path:(?!nm|api|static|assets).*}/**", method = RequestMethod.GET)
    public String vueIndex(String path) {
        return "index";
    }
}
