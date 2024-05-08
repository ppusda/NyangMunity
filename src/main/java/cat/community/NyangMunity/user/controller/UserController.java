package cat.community.NyangMunity.user.controller;

import cat.community.NyangMunity.global.provider.CookieProvider;
import cat.community.NyangMunity.global.provider.JwtTokenProvider;
import cat.community.NyangMunity.user.entity.User;
import cat.community.NyangMunity.user.request.UserForm;
import cat.community.NyangMunity.user.request.UserSession;
import cat.community.NyangMunity.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CookieProvider cookieProvider;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/join")
    private void userJoin(@RequestBody @Valid UserForm userForm) {
        userService.register(userForm);
    }

    @PostMapping("/login")
    private ResponseEntity<Object> userLogin(@RequestBody @Valid UserForm userForm) {
        Long userId = userService.userLogin(userForm);
        ResponseCookie cookie = cookieProvider.createCookie(jwtTokenProvider.createAccessToken(userId));

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @PostMapping("/check")
    private ResponseEntity<Object> loginCheck(UserSession session) {
        if(!session.token.isEmpty()) {
            ResponseCookie cookie = cookieProvider.createCookie(session.token);
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(userService.userInfo(session.id).getNickname());
        }else {
            return ResponseEntity.ok()
                    .build();
        }
    }

    @PostMapping("/info")
    private User userInfo(UserSession session) {
        return userService.userInfo(session.id);
    }

    @PostMapping("/pwdCheck")
    private boolean userCheck(@RequestParam String pwdCheck, UserSession session) {
        return userService.userCheck(pwdCheck, session.id);
    }

    @PostMapping("/edit")
    private void userEdit(UserForm userForm, UserSession session) {
        userService.userEdit(userForm, session.id);
    }

    @PostMapping("/cancel")
    private void userCancel(UserSession session) {
        userService.userCancel(session.id);
    }

    @PostMapping("/logout")
    private void userLogout(UserSession session) {
        userService.userLogout(session.id);
    }

    @GetMapping("/kakaoLogin")
    public void loginKakao(@RequestParam String code) {
        userService.kakaoLogin(code);
    }
}
