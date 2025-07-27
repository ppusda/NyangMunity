package cat.community.nyangmunity.member.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cat.community.nyangmunity.member.service.AuthService;
import lombok.RequiredArgsConstructor;

import cat.community.nyangmunity.member.response.GoogleUserResponse;
import cat.community.nyangmunity.member.response.KakaoUserResponse;

import cat.community.nyangmunity.member.response.MemberAuthenticationResponse;
import cat.community.nyangmunity.member.service.MemberFacadeService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
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
	public MemberAuthenticationResponse redirectKakaoLogin(@RequestParam String code) {
		KakaoUserResponse kakaoUserResponse = authService.loginWithKakao(code).block();
		return memberFacadeService.socialLogin("KAKAO", kakaoUserResponse);
	}

	@GetMapping("/googleLogin")
	public MemberAuthenticationResponse redirectGoogleLogin(@RequestParam String code) {
		GoogleUserResponse googleUserResponse = authService.loginWithGoogle(code).block();
		return memberFacadeService.socialLogin("GOOGLE", googleUserResponse);
	}
}
