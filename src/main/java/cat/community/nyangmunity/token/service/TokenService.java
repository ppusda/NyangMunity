package cat.community.nyangmunity.token.service;

import cat.community.nyangmunity.global.exception.UnauthorizedUserException;
import cat.community.nyangmunity.token.entity.Token;
import cat.community.nyangmunity.token.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;

    /***
     * @param memberId // Redis의 토큰 조회용 메서드
     * @return // Token 객체를 return 하며, 토큰 정보가 Redis 내부에 없을 시 인증 되지 않은 유저로 판별
     */
    public Token getToken(String memberId) {
        return tokenRepository.findByMemberId(Long.parseLong(memberId))
                .orElseThrow(UnauthorizedUserException::new);
    }

    @Transactional
    public void deleteToken(Long userId) {
        tokenRepository.deleteByMemberId(userId);
    }

    @Transactional
    public void register(String refreshToken, Long memberId) {
        Token token = Token.builder()
                .refreshToken(refreshToken)
                .memberId(memberId)
                .build();

        tokenRepository.save(token);
    }
}
