package cat.community.nyangmunity.token.service;

import cat.community.nyangmunity.global.exception.UnauthorizedAccessException;
import cat.community.nyangmunity.global.provider.JwtTokenProvider;
import cat.community.nyangmunity.token.entity.Token;
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

    /***
     * @param userId // 토큰 업데이트를 위한 메서드, 기존 토큰 교체 작업 진행
     * @param refreshToken
     */
    private void updateRefreshToken(Long userId, String refreshToken) {
        tokenService.deleteToken(userId);
        tokenService.register(refreshToken, userId);
    }

}