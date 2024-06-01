package cat.community.NyangMunity.token.service;

import cat.community.NyangMunity.global.exception.UnauthorizedAccessException;
import cat.community.NyangMunity.global.provider.JwtTokenProvider;
import cat.community.NyangMunity.token.entity.Token;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class TokenValidationService {

    private final TokenService tokenService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * @param userId // 리프레시 토큰을 검사하기 위한 메서드
     * @param refreshToken // 리프레시 토큰이 없을 때, 기존과 다를 때, 변조 되었을 때
     * @throws UnauthorizedAccessException // 잘못된 접근으로 인식하여 예외 처리하고, 기존 토큰을 제거한다.
     */
    public void validateRefreshToken(String userId, String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throwUnauthorizedAccessException(Long.parseLong(userId));
        }

        Token token = tokenService.getToken(userId);

        if (!refreshToken.equals(token.getRefreshToken()) ||
                !jwtTokenProvider.validateToken(token.getRefreshToken())
        ) {
            throwUnauthorizedAccessException(Long.parseLong(userId));
        }
    }

    private void throwUnauthorizedAccessException(Long userId) {
        tokenService.deleteToken(userId);
        throw new UnauthorizedAccessException();
    }

    /***
     * @param userId // 토큰을 초기화 하기 위한 메서드, RTR 기법을 적용
     * @return
     */
    public String[] refreshTokens(Long userId) {
        String accessToken = jwtTokenProvider.createAccessToken(userId);
        String refreshToken = jwtTokenProvider.createRefreshToken(userId);
        updateRefreshToken(userId, refreshToken);

        return new String[] {accessToken, refreshToken};
    }

    private void updateRefreshToken(Long memberId, String refreshToken) {
        tokenService.deleteToken(memberId);
        tokenService.register(refreshToken, memberId);
    }

}