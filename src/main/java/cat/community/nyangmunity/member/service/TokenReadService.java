package cat.community.nyangmunity.member.service;

import org.springframework.stereotype.Service;

import cat.community.nyangmunity.global.exception.global.InvalidRequestException;
import cat.community.nyangmunity.global.exception.global.UnauthorizedException;
import cat.community.nyangmunity.member.entity.Token;
import cat.community.nyangmunity.member.repository.TokenRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenReadService {

	private final TokenRepository tokenRepository;

	/**
	 * 토큰 조회용 메서드
	 * @param memberId 토큰과 연관 된 회원 아이디
	 * @throws UnauthorizedException 인증되지 않은 사용자 정보
	 * @return 토큰 정보
	 */
	public Token findTokenByMemberId(String memberId) {
		return tokenRepository.findByMemberId(Long.parseLong(memberId))
			.orElseThrow(UnauthorizedException::new);
	}

	/**
	 * 갱신 토큰 조회용 메서드
	 * @param refreshToken 갱신 토큰
	 * @throws InvalidRequestException 잘못된 접근 토큰을 전달 받았을 때
	 * @return 토큰 정보
	 */
	public Token findTokenByRefreshToken(String refreshToken) {
		return tokenRepository.findById(refreshToken)
			.orElseThrow(InvalidRequestException::new);
	}

}
