package cat.community.NyangMunity.token.service;

import cat.community.NyangMunity.global.exception.UnauthorizedUserException;
import cat.community.NyangMunity.token.entity.Token;
import cat.community.NyangMunity.token.repository.TokenRepository;
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
        return tokenRepository.findByUserId(Long.parseLong(memberId))
                .orElseThrow(UnauthorizedUserException::new);
    }

    @Transactional
    public void deleteToken(Long userId) {
        tokenRepository.deleteByUserId(userId);
    }

    @Transactional
    public void register(String refreshToken, Long memberId) {
        Token token = Token.builder()
                .refreshToken(refreshToken)
                .userId(memberId)
                .build();

        tokenRepository.save(token);
    }
}
