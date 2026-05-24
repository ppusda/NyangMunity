package cat.community.nyangmunity.member.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cat.community.nyangmunity.global.exception.global.BadRequestException;
import cat.community.nyangmunity.global.provider.CookieProvider;
import cat.community.nyangmunity.member.response.AuthUrlResponse;
import cat.community.nyangmunity.member.response.MemberAuthenticationResponse;
import cat.community.nyangmunity.member.response.MemberInfoResponse;
import cat.community.nyangmunity.member.response.google.GoogleUserResponse;
import cat.community.nyangmunity.member.response.kakao.KakaoUserResponse;
import cat.community.nyangmunity.member.service.AuthService;
import cat.community.nyangmunity.member.service.MemberFacadeService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private static final String KAKAO = "kakao";
	private static final String GOOGLE = "google";

	private final AuthService authService;
	private final CookieProvider cookieProvider;
	private final MemberFacadeService memberFacadeService;

	@GetMapping("/{provider}/url")
	public AuthUrlResponse requestAuthorizationUrl(@PathVariable String provider) {
		return new AuthUrlResponse(switch (provider) {
			case KAKAO -> authService.requestKakaoAuthorizationUrl();
			case GOOGLE -> authService.requestGoogleAuthorizationUrl();
			default -> throw new BadRequestException("지원하지 않는 소셜 로그인 제공자입니다.");
		});
	}

	@GetMapping("/{provider}/callback")
	public ResponseEntity<MemberInfoResponse> socialLoginCallback(
		@PathVariable String provider,
		@RequestParam String code
	) {
		MemberAuthenticationResponse authenticationResponse = switch (provider) {
			case KAKAO -> {
				KakaoUserResponse kakaoUser = authService.loginWithKakao(code).block();
				yield memberFacadeService.socialLogin("KAKAO", kakaoUser);
			}
			case GOOGLE -> {
				GoogleUserResponse googleUser = authService.loginWithGoogle(code).block();
				yield memberFacadeService.socialLogin("GOOGLE", googleUser);
			}
			default -> throw new BadRequestException("지원하지 않는 소셜 로그인 제공자입니다.");
		};

		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE,
				cookieProvider.createAccessTokenCookie(authenticationResponse.memberTokens().accessToken()).toString())
			.header(HttpHeaders.SET_COOKIE,
				cookieProvider.createRefreshTokenCookie(authenticationResponse.memberTokens().refreshToken()).toString())
			.body(authenticationResponse.memberInfoResponse());
	}
}
