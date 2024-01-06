package cat.community.NyangMunity.global.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
