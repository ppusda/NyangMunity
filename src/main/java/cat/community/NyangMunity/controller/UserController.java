package cat.community.NyangMunity.controller;

import cat.community.NyangMunity.config.JwtTokenProvider;
import cat.community.NyangMunity.request.UserForm;
import cat.community.NyangMunity.request.UserSession;
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
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/join")
    private void userJoin(@RequestBody @Valid UserForm userForm) throws IOException {
        userService.register(userForm);
    }

    @PostMapping("/login")
    private ResponseEntity<Object> userLogin(@RequestBody @Valid UserForm userForm) throws IOException {
        Long userId = userService.userLogin(userForm);

        ResponseCookie cookie = ResponseCookie.from("SESSION", jwtTokenProvider.createAccessToken(userId))
                .domain("localhost") // todo 향후 서버 환경에 따른 분리 필요
                .path("/")
                .httpOnly(false) // javascript가 cookie 값에 접근하지 못하게 하는 설정.
                .secure(false)
                .maxAge(Duration.ofDays(1))
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @PostMapping("/check")
    private ResponseEntity<Object> loginCheck(UserSession session) {
        if(!session.token.isEmpty()) {
            ResponseCookie cookie = ResponseCookie.from("SESSION", session.token)
                    .domain("localhost") // todo 향후 서버 환경에 따른 분리 필요
                    .path("/")
                    .httpOnly(false)
                    .secure(false)
                    .maxAge(Duration.ofDays(1))
                    .sameSite("Strict")
                    .build();
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(userService.userCheck(session.id));
        }else {
            return ResponseEntity.ok()
                    .build();
        }
    }

    @PostMapping("/logout") // todo access, refresh로 변경 후 작업할 것임
    private void userLogout(UserSession session) {
        userService.userLogout(session.id);
    }

    @RequestMapping("/kakaoLogin")
    public String loginKakao(){
        return "/main";
    }
}
