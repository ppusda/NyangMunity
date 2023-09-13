package cat.community.NyangMunity.controller;

import cat.community.NyangMunity.request.UserForm;
import cat.community.NyangMunity.service.JoinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/nm/user")
@RequiredArgsConstructor
public class JoinController {

    private final JoinService joinService;

    @PostMapping("/join")
    private void userJoinConfirm(@RequestBody @Valid UserForm userForm) throws IOException {
        joinService.register(userForm);
    }

}
