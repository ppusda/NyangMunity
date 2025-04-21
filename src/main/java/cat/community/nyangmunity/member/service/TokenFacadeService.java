package cat.community.nyangmunity.member.service;

import org.springframework.stereotype.Service;

import cat.community.nyangmunity.global.exception.global.ForbiddenException;
import cat.community.nyangmunity.global.exception.global.InternalServerErrorException;
import cat.community.nyangmunity.global.exception.member.LoginExpiredException;
import cat.community.nyangmunity.global.provider.JwtTokenProvider;
import cat.community.nyangmunity.member.entity.Token;
import cat.community.nyangmunity.member.response.MemberAuthenticationResponse;
import cat.community.nyangmunity.member.response.MemberInfoResponse;
import cat.community.nyangmunity.member.response.MemberTokens;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenFacadeService {

	private final TokenReadService tokenReadService;
	private final TokenCommandService tokenCommandService;

	private final JwtTokenProvider jwtTokenProvider;

	/**
	 * 접근 토큰, 갱신 토큰 생성 메서드
	 * @param memberId 회원 아이디
	 * @return 생성된 토큰들
	 */
	public MemberTokens createTokens(Long memberId) {
		String accessToken = jwtTokenProvider.createAccessToken(memberId);
		String refreshToken = jwtTokenProvider.createRefreshToken(memberId);

		tokenCommandService.register(refreshToken, memberId);

		return MemberTokens.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	/**
	 * 접근 토큰 재갱신을 위한 메서드
	 * @param token 회원 아이디로 조회 된 토큰 정보
	 * @return 재갱신 된 인증 정보
	 */
	public MemberAuthenticationResponse reissueToken(Token token) {
		Long memberId = token.getMemberId();
		switch (jwtTokenProvider.validateToken(token.getRefreshToken())) {
			case ACCEPTED -> {
				return MemberAuthenticationResponse.builder()
					.memberInfoResponse(MemberInfoResponse.builder()
						.id(memberId)
						.build()
					)
					.memberTokens(MemberTokens.builder()
						.accessToken(jwtTokenProvider.createAccessToken(memberId))
						.refreshToken(token.getRefreshToken())
						.build()
					)
					.build();
			}
			case DENIED -> throw new ForbiddenException();
			case EXPIRED -> throw new LoginExpiredException();
		}

		throw new InternalServerErrorException();
	}

	public Token findTokenByMemberId(String memberId) {
		return tokenReadService.findTokenByMemberId(memberId);
	}

	public void deleteToken(String memberId) {
		tokenCommandService.delete(findTokenByMemberId(memberId));
	}
}
