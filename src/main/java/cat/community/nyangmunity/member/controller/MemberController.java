package cat.community.nyangmunity.member.controller;

import java.security.Principal;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cat.community.nyangmunity.global.provider.CookieProvider;
import cat.community.nyangmunity.member.request.EditRequest;
import cat.community.nyangmunity.member.request.JoinRequest;
import cat.community.nyangmunity.member.request.LoginRequest;
import cat.community.nyangmunity.member.response.MemberAuthenticationResponse;
import cat.community.nyangmunity.member.response.MemberInfoResponse;
import cat.community.nyangmunity.member.service.MemberFacadeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

	private final MemberFacadeService memberFacadeService;
	private final CookieProvider cookieProvider;

	@PostMapping("/login")
	private ResponseEntity<MemberInfoResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
		MemberAuthenticationResponse memberAuthenticationResponse = memberFacadeService.login(loginRequest);

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

	@PostMapping("/logout")
	private void logout(@CookieValue("refreshToken") String refreshToken) {
		memberFacadeService.logout(refreshToken);
	}

	@PostMapping("/join")
	private void memberJoin(@RequestBody @Valid JoinRequest joinRequest) {
		memberFacadeService.join(joinRequest);
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/cancel")
	private void cancel(Principal principal) {
		memberFacadeService.cancel(Long.parseLong(principal.getName()));
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/profile")
	private MemberInfoResponse info(Principal principal) {
		return memberFacadeService.findProfile(Long.parseLong(principal.getName()));
	}

	@PreAuthorize("isAuthenticated()")
	@PatchMapping("/profile")
	private void edit(@RequestBody @Valid EditRequest editRequest, Principal principal) {
		memberFacadeService.edit(editRequest, Long.parseLong(principal.getName()));
	}

}
