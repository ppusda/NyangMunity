package cat.community.nyangmunity.member.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cat.community.nyangmunity.global.provider.CookieProvider;
import cat.community.nyangmunity.member.response.MemberInfoResponse;
import cat.community.nyangmunity.member.service.AuthService;
import lombok.RequiredArgsConstructor;

import cat.community.nyangmunity.member.response.google.GoogleUserResponse;
import cat.community.nyangmunity.member.response.kakao.KakaoUserResponse;

import cat.community.nyangmunity.member.response.MemberAuthenticationResponse;
import cat.community.nyangmunity.member.service.MemberFacadeService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final CookieProvider cookieProvider;
	private final MemberFacadeService memberFacadeService;

	@GetMapping("/kakao/url")
	public String requestKakaoAuthorizationUrl() {
		return authService.requestKakaoAuthorizationUrl();
	}

	@GetMapping("/google/url")
	public String requestGoogleAuthorizationUrl() {
		return authService.requestGoogleAuthorizationUrl();
	}

	@GetMapping("/kakaoLogin")
	public ResponseEntity<MemberInfoResponse> redirectKakaoLogin(@RequestParam String code) {
		KakaoUserResponse kakaoUserResponse = authService.loginWithKakao(code).block();
		MemberAuthenticationResponse authenticationResponse = memberFacadeService.socialLogin("KAKAO",
			kakaoUserResponse);

		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, String.valueOf(
				cookieProvider.createAccessTokenCookie(authenticationResponse.memberTokens()
					.accessToken())
			))
			.header(HttpHeaders.SET_COOKIE, String.valueOf(
				cookieProvider.createRefreshTokenCookie(authenticationResponse.memberTokens()
					.refreshToken())
			))
			.body(authenticationResponse.memberInfoResponse());
	}

	@GetMapping("/googleLogin")
	public ResponseEntity<MemberInfoResponse> redirectGoogleLogin(@RequestParam String code) {
		GoogleUserResponse googleUserResponse = authService.loginWithGoogle(code).block();
		MemberAuthenticationResponse authenticationResponse = memberFacadeService.socialLogin("GOOGLE",
			googleUserResponse);

		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, String.valueOf(
				cookieProvider.createAccessTokenCookie(authenticationResponse.memberTokens()
					.accessToken())
			))
			.header(HttpHeaders.SET_COOKIE, String.valueOf(
				cookieProvider.createRefreshTokenCookie(authenticationResponse.memberTokens()
					.refreshToken())
			))
			.body(authenticationResponse.memberInfoResponse());
	}
}
