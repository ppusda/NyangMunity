package cat.community.NyangMunity.controller;

import cat.community.NyangMunity.controller.form.JoinForm;
import cat.community.NyangMunity.controller.form.UserForm;
import cat.community.NyangMunity.domain.User;
import cat.community.NyangMunity.service.JoinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/nm/user")
@RequiredArgsConstructor
public class JoinController {

    private final JoinService joinService;

    @GetMapping("/join")
    private void userJoin(){
        System.out.println("test");
    }

    @PostMapping("/join")
    private void userJoinConfirm(@RequestBody @Valid UserForm userForm, HttpServletResponse response) throws IOException {
        joinService.register(userForm);
    }

}
