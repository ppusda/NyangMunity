package cat.community.nyangmunity.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cat.community.nyangmunity.member.entity.Token;
import cat.community.nyangmunity.member.repository.TokenRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenCommandService {

    private final TokenRepository tokenRepository;

    /**
	 * 토큰 제거용 메서드 (로그아웃 시 사용)
     * @param token 삭제할 토큰 객체
     */
    @Transactional
    public void delete(Token token) {
        tokenRepository.delete(token);
    }

    /**
	 * 토큰 등록용 메서드 (로그인 시 사용)
     * @param refreshToken 생성한 갱신 토큰
     * @param memberId 토큰과 연관 된 회원 아이디
     */
    @Transactional
    public void register(String refreshToken, Long memberId) {
        Token token = Token.builder()
                .refreshToken(refreshToken)
                .memberId(memberId)
                .build();

        tokenRepository.save(token);
    }
}
