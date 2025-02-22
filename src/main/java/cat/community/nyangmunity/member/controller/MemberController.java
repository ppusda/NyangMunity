package cat.community.nyangmunity.member.controller;

import cat.community.nyangmunity.global.provider.CookieProvider;
import cat.community.nyangmunity.member.entity.Member;
import cat.community.nyangmunity.member.request.MemberEditForm;
import cat.community.nyangmunity.member.request.JoinRequest;
import cat.community.nyangmunity.member.request.LoginRequest;
import cat.community.nyangmunity.member.response.MemberCheckResponse;
import cat.community.nyangmunity.member.response.MemberInfos;
import cat.community.nyangmunity.member.response.MemberLoginResponse;
import cat.community.nyangmunity.member.response.MemberResponse;
import cat.community.nyangmunity.member.service.MemberService;
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
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final CookieProvider cookieProvider;

    @PostMapping("/join")
    private void memberJoin(@RequestBody @Valid JoinRequest joinRequest) {
        memberService.joinMember(joinRequest);
    }

    @PostMapping("/login")
    private ResponseEntity<MemberInfos> userLogin(@RequestBody @Valid LoginRequest loginRequest) {
        MemberLoginResponse memberLoginResponse = memberService.userLogin(loginRequest);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, String.valueOf(
                        cookieProvider.createAccessTokenCookie(memberLoginResponse.memberTokens()
                                .accessToken())
                ))
                .header(HttpHeaders.SET_COOKIE, String.valueOf(
                        cookieProvider.createRefreshTokenCookie(memberLoginResponse.memberTokens()
                                .refreshToken())
                ))
                .body(memberLoginResponse.memberInfos());
    }

    @PostMapping("/check")
    private MemberCheckResponse loginCheck(Principal principal) {
        if (principal != null) {
            Member member = memberService.getUserById(Long.parseLong(principal.getName()));

            return MemberCheckResponse.builder()
                    .nickname(member.getNickname())
                    .result(true)
                    .build();
        }

        return MemberCheckResponse.builder()
                .result(false)
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    private MemberResponse userInfo(Principal principal) {
        return MemberResponse.toUserResponse(
                memberService.getUserById(Long.parseLong(principal.getName()))
        );
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/profile")
    private void userEdit(@RequestBody @Valid MemberEditForm memberEditForm, Principal principal) {
        memberService.userEdit(memberEditForm, Long.parseLong(principal.getName()));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/cancel") // 회원 탈퇴
    private void userCancel(Principal principal) {
        memberService.userCancel(Long.parseLong(principal.getName()));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    private void userLogout(Principal principal) {
        memberService.userLogout(Long.parseLong(principal.getName()));
    }
}
