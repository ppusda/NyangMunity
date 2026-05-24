package cat.community.nyangmunity.member.service;

import org.springframework.stereotype.Service;

import cat.community.nyangmunity.global.data.JwtValidateStatus;
import cat.community.nyangmunity.global.exception.global.InternalServerErrorException;
import cat.community.nyangmunity.global.exception.global.UnauthorizedException;
import cat.community.nyangmunity.global.exception.member.LoginExpiredException;
import cat.community.nyangmunity.global.provider.JwtTokenProvider;
import cat.community.nyangmunity.member.entity.Token;
import cat.community.nyangmunity.member.redisRepository.AccessTokenBlacklistRepository;
import cat.community.nyangmunity.member.response.MemberAuthenticationResponse;
import cat.community.nyangmunity.member.response.MemberInfoResponse;
import cat.community.nyangmunity.member.response.MemberTokens;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenFacadeService {

	private final TokenQueryService tokenQueryService;
	private final TokenCommandService tokenCommandService;
	private final AccessTokenBlacklistRepository accessTokenBlacklistRepository;

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
	 * @param refreshToken 재갱신 토큰
	 * @return 재갱신 된 인증 정보
	 */
	public MemberAuthenticationResponse reissueToken(String refreshToken) {
		Token token = validateRefreshToken(refreshToken);

		return MemberAuthenticationResponse.builder()
			.memberInfoResponse(MemberInfoResponse.builder()
				.id(token.getMemberId())
				.build()
			)
			.memberTokens(MemberTokens.builder()
				.accessToken(jwtTokenProvider.createAccessToken(token.getMemberId()))
				.refreshToken(token.getRefreshToken())
				.build()
			)
			.build();
	}

	/**
	 * 토큰 삭제를 위한 메서드
	 * @param refreshToken 삭제할 갱신 토큰
	 */
	public void deleteToken(String refreshToken) {
		Token token = tokenQueryService.findTokenByRefreshToken(refreshToken);
		tokenCommandService.delete(token);
	}

	/**
	 * 로그아웃 시 access 토큰을 블랙리스트에 등록한다.
	 * 이미 만료·위조된 토큰은 등록 의미가 없어 무시한다.
	 */
	public void blacklistAccessToken(String accessToken) {
		if (accessToken == null) {
			return;
		}
		if (jwtTokenProvider.validateToken(accessToken) != JwtValidateStatus.ACCEPTED) {
			return;
		}
		String jti = jwtTokenProvider.getJti(accessToken);
		long remaining = jwtTokenProvider.getRemainingMillis(accessToken);
		accessTokenBlacklistRepository.blacklist(jti, remaining);
	}

	private Token validateRefreshToken(String refreshToken) {
		switch (jwtTokenProvider.validateToken(refreshToken)) {
			case ACCEPTED -> {
				String memberId = jwtTokenProvider.getClaims(refreshToken).getSubject();
				return findTokenByMemberId(memberId);
			}
			case DENIED -> throw new UnauthorizedException();
			case EXPIRED -> throw new LoginExpiredException();
		}
		throw new InternalServerErrorException();
	}

	public Token findTokenByMemberId(String memberId) {
		return tokenQueryService.findTokenByMemberId(memberId);
	}
}
