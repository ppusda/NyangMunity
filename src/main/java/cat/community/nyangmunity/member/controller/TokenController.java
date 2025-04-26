package cat.community.nyangmunity.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cat.community.nyangmunity.global.exception.global.UnauthorizedException;
import cat.community.nyangmunity.member.response.MemberAuthenticationResponse;
import cat.community.nyangmunity.member.service.TokenFacadeService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tokens")
public class TokenController {

	private final TokenFacadeService tokenFacadeService;

	/**
	 * 토큰 재갱신을 위한 API
	 *
	 * @param refreshToken 쿠키에 저장 된 refreshToken
	 * @return 갱신된 인증 정보
	 */
	@PostMapping
	public ResponseEntity<MemberAuthenticationResponse> reissue(@CookieValue("refreshToken") String refreshToken) {
		if (refreshToken != null) {
			MemberAuthenticationResponse reissueResponse = tokenFacadeService.reissueToken(refreshToken);
			return ResponseEntity.ok().body(reissueResponse);
		}

		throw new UnauthorizedException();
	}
}
