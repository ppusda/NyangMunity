package cat.community.nyangmunity.member.controller;

import java.security.Principal;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cat.community.nyangmunity.global.provider.CookieProvider;
import cat.community.nyangmunity.member.entity.Member;
import cat.community.nyangmunity.member.request.JoinRequest;
import cat.community.nyangmunity.member.request.LoginRequest;
import cat.community.nyangmunity.member.request.MemberEditForm;
import cat.community.nyangmunity.member.response.MemberAuthenticationResponse;
import cat.community.nyangmunity.member.response.MemberCheckResponse;
import cat.community.nyangmunity.member.response.MemberInfoResponse;
import cat.community.nyangmunity.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
    private ResponseEntity<MemberInfoResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        MemberAuthenticationResponse memberAuthenticationResponse = memberService.login(loginRequest);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, String.valueOf(
                        cookieProvider.createAccessTokenCookie(memberAuthenticationResponse.memberTokens()
                                .accessToken())
                ))
                .header(HttpHeaders.SET_COOKIE, String.valueOf(
                        cookieProvider.createRefreshTokenCookie(memberAuthenticationResponse.memberTokens()
                                .refreshToken())
                ))
                .body(memberAuthenticationResponse.memberInfoResponse());
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
    private MemberInfoResponse info(Principal principal) {
        return MemberInfoResponse.from(
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

    @PostMapping("/logout")
    private void logout(Principal principal) {
        memberService.logout(principal.getName());
    }
}
