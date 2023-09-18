package cat.community.NyangMunity.controller;

import cat.community.NyangMunity.request.SessionId;
import cat.community.NyangMunity.request.UserForm;
import cat.community.NyangMunity.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.time.Duration;

@Slf4j
@RestController
@RequestMapping("/nm/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    private void userJoinConfirm(@RequestBody @Valid UserForm userForm) throws IOException {
        userService.register(userForm);
    }

    @PostMapping("/login")
    private ResponseEntity<Object> loginComplete(@RequestBody @Valid UserForm userForm) throws IOException {
        String accessToken = userService.userLogin(userForm);
        ResponseCookie cookie = ResponseCookie.from("SESSION", accessToken)
                .domain("localhost") // todo 향후 서버 환경에 따른 분리 필요
                .path("/")
                .httpOnly(false) // javascript가 cookie 값에 접근하지 못하게 하는 설정.
                .secure(false)
                .maxAge(Duration.ofDays(30))
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @PostMapping("/check")
    private String loginCheck(@RequestBody SessionId SID) throws IOException{
        return userService.userCheck(SID.getSID());
    }

    @RequestMapping("/kakaoLogin")
    public String loginKakao(){
        return "/main";
    }
}
