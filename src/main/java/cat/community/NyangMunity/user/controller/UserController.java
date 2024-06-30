package cat.community.NyangMunity.user.controller;

import cat.community.NyangMunity.global.provider.CookieProvider;
import cat.community.NyangMunity.user.entity.User;
import cat.community.NyangMunity.user.request.UserEditForm;
import cat.community.NyangMunity.user.request.UserForm;
import cat.community.NyangMunity.user.request.UserJoinForm;
import cat.community.NyangMunity.user.request.UserLoginForm;
import cat.community.NyangMunity.user.response.UserCheckResponse;
import cat.community.NyangMunity.user.response.UserTokenResponse;
import cat.community.NyangMunity.user.response.UserResponse;
import cat.community.NyangMunity.user.service.UserService;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CookieProvider cookieProvider;

    @PostMapping("/join")
    private void userJoin(@RequestBody @Valid UserJoinForm userJoinForm) {
        userService.register(userJoinForm);
    }

    @PostMapping("/login")
    private ResponseEntity<?> userLogin(@RequestBody @Valid UserLoginForm userLoginForm) {
        UserTokenResponse userTokenResponse = userService.userLogin(userLoginForm);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, String.valueOf(
                        cookieProvider.createAccessTokenCookie(userTokenResponse.accessToken())
                ))
                .header(HttpHeaders.SET_COOKIE, String.valueOf(
                        cookieProvider.createRefreshTokenCookie(userTokenResponse.refreshToken())
                ))
                .build();
    }

    @PostMapping("/check")
    private UserCheckResponse loginCheck(Principal principal) {
        if (principal != null) {
            User user = userService.getUserById(Long.parseLong(principal.getName()));

            return UserCheckResponse.builder()
                    .nickname(user.getNickname())
                    .result(true)
                    .build();
        }

        return UserCheckResponse.builder()
                .result(false)
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    private UserResponse userInfo(Principal principal) {
        return UserResponse.toUserResponse(
                userService.getUserById(Long.parseLong(principal.getName()))
        );
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/profile")
    private void userEdit(@RequestBody @Valid UserEditForm userEditForm, Principal principal) {
        userService.userEdit(userEditForm, Long.parseLong(principal.getName()));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/cancel") // 회원 탈퇴
    private void userCancel(Principal principal) {
        userService.userCancel(Long.parseLong(principal.getName()));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    private void userLogout(Principal principal) {
        userService.userLogout(Long.parseLong(principal.getName()));
    }
}
