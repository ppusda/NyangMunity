package cat.community.NyangMunity.controller;

import cat.community.NyangMunity.config.CookieProvider;
import cat.community.NyangMunity.config.JwtTokenProvider;
import cat.community.NyangMunity.config.KakaoAuthProvider;
import cat.community.NyangMunity.domain.User;
import cat.community.NyangMunity.request.UserForm;
import cat.community.NyangMunity.request.UserSession;
import cat.community.NyangMunity.response.KakaoTokenResponse;
import cat.community.NyangMunity.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/nm/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final KakaoAuthProvider kakaoAuthProvider;
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieProvider cookieProvider;

    @PostMapping("/join")
    private void userJoin(@RequestBody @Valid UserForm userForm) throws IOException {
        userService.register(userForm);
    }

    @PostMapping("/login")
    private ResponseEntity<Object> userLogin(@RequestBody @Valid UserForm userForm) throws IOException {
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
    public void loginKakao(@RequestParam String code) throws JSONException {
        KakaoTokenResponse tokenResponse = kakaoAuthProvider.getToken(code);
        log.info(tokenResponse.getAccess_token());
        log.info(tokenResponse.getRefresh_token());
    }

}
