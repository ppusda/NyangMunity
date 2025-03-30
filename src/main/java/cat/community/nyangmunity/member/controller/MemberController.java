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
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final CookieProvider cookieProvider;

    @PostMapping("/join")
    private void memberJoin(@RequestBody @Valid JoinRequest joinRequest) {
        memberService.join(joinRequest);
    }

    @PostMapping("/login")
    private ResponseEntity<MemberInfos> login(@RequestBody @Valid LoginRequest loginRequest) {
        MemberLoginResponse memberLoginResponse = memberService.login(loginRequest);

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

    @GetMapping("/check")
    private MemberCheckResponse loginCheck(Principal principal) {
        if (principal != null) {
            Member member = memberService.findMemberById(Long.parseLong(principal.getName()));

            return MemberCheckResponse.builder()
                .memberId(member.getId())
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
    private MemberResponse info(Principal principal) {
        return MemberResponse.toUserResponse(
                memberService.findMemberById(Long.parseLong(principal.getName()))
        );
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/profile")
    private void edit(@RequestBody @Valid MemberEditForm memberEditForm, Principal principal) {
        memberService.edit(memberEditForm, Long.parseLong(principal.getName()));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/cancel")
    private void cancel(Principal principal) {
        memberService.cancel(Long.parseLong(principal.getName()));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    private void logout(Principal principal) {
        memberService.logout(Long.parseLong(principal.getName()));
    }
}
