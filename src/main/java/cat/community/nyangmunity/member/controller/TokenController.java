package cat.community.nyangmunity.member.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cat.community.nyangmunity.global.provider.CookieProvider;
import cat.community.nyangmunity.member.response.MemberAuthenticationResponse;
import cat.community.nyangmunity.member.response.MemberTokens;
import cat.community.nyangmunity.member.service.TokenFacadeService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tokens")
public class TokenController {

	private final TokenFacadeService tokenFacadeService;
	private final CookieProvider cookieProvider;

	/**
	 * 토큰 재갱신을 위한 API. 갱신된 access·refresh 토큰을 Set-Cookie 헤더로 함께 내려,
	 * 클라이언트가 HttpOnly refresh 쿠키를 직접 조작하지 못해도 일관되게 갱신된다.
	 */
	@PostMapping
	public ResponseEntity<MemberAuthenticationResponse> reissue(@CookieValue("refreshToken") String refreshToken) {
		MemberAuthenticationResponse reissueResponse = tokenFacadeService.reissueToken(refreshToken);
		MemberTokens tokens = reissueResponse.memberTokens();

		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, cookieProvider.createAccessTokenCookie(tokens.accessToken()).toString())
			.header(HttpHeaders.SET_COOKIE, cookieProvider.createRefreshTokenCookie(tokens.refreshToken()).toString())
			.body(reissueResponse);
	}
}
