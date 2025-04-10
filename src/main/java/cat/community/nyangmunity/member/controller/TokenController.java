package cat.community.nyangmunity.member.controller;

import java.security.Principal;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cat.community.nyangmunity.global.exception.global.UnauthorizedException;
import cat.community.nyangmunity.global.provider.CookieProvider;
import cat.community.nyangmunity.member.entity.Token;
import cat.community.nyangmunity.member.response.MemberAuthenticationResponse;
import cat.community.nyangmunity.member.response.MemberInfoResponse;
import cat.community.nyangmunity.member.service.TokenFacadeService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tokens")
public class TokenController {

	private final TokenFacadeService tokenFacadeService;
	private final CookieProvider cookieProvider;

	/**
	 * 토큰 재갱신을 위한 API
	 *
	 * @param principal 접근 토큰 검증을 끝낸 인증 객체
	 * @return 갱신된 인증 정보
	 */
	@PreAuthorize("isAuthenticated()")
	@PostMapping
	public ResponseEntity<MemberInfoResponse> reissue(Principal principal) {
		if (principal != null) {
			Token token = tokenFacadeService.findTokenByMemberId(principal.getName());
			MemberAuthenticationResponse reissueResponse = tokenFacadeService.reissueToken(token);

			return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, String.valueOf(
					cookieProvider.createAccessTokenCookie(reissueResponse.memberTokens()
						.accessToken())
				))
				.body(reissueResponse.memberInfoResponse());
		}

		throw new UnauthorizedException();
	}
}
