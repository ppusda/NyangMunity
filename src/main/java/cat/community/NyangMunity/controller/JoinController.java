package cat.community.NyangMunity.controller;

import cat.community.NyangMunity.controller.form.JoinForm;
import cat.community.NyangMunity.controller.form.UserForm;
import cat.community.NyangMunity.domain.User;
import cat.community.NyangMunity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class JoinController {

    private final UserRepository userRepository;

    @GetMapping("/join")
    private String userJoin(Model model){

        model.addAttribute("userForm", new UserForm());
        return "/user/join";
    }

    @PostMapping("/join")
    private String userJoinConfirm(@Valid JoinForm joinForm, HttpServletResponse response) throws IOException {

        User user = new User();

        user.setEmail(joinForm.getEmail());
        user.setPassword(joinForm.getPassword());
        user.setNickname(joinForm.getNickname());
        user.setCreateDate(LocalDateTime.now());

        userRepository.save(user);

        return "/user/joinConfirm";
    }

}
