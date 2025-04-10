package cat.community.nyangmunity.member.service;

import org.springframework.stereotype.Service;

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
     * @return Token 정보
     * @exception UnauthorizedException 인증되지 않은 사용자 정보
     */
    public Token findTokenByMemberId(String memberId) {
        return tokenRepository.findByMemberId(Long.parseLong(memberId))
                .orElseThrow(UnauthorizedException::new);
    }


}
