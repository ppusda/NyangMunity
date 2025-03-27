package cat.community.nyangmunity.member.service;

import cat.community.nyangmunity.global.exception.UnauthorizedAccessException;
import cat.community.nyangmunity.global.provider.JwtTokenProvider;
import cat.community.nyangmunity.member.entity.Token;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class TokenValidationService {

    private final TokenService tokenService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * @param memberId // 리프레시 토큰을 검사하기 위한 메서드
     * @param refreshToken // 리프레시 토큰이 없을 때, 기존과 다를 때, 변조 되었을 때
     * @throws UnauthorizedAccessException // 잘못된 접근으로 인식하여 예외 처리하고, 기존 토큰을 제거한다.
     */
    public void validateRefreshToken(String memberId, String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throwUnauthorizedAccessException(Long.parseLong(memberId));
        }

        Token token = tokenService.getToken(memberId);

        if (!refreshToken.equals(token.getRefreshToken()) ||
                !jwtTokenProvider.validateToken(token.getRefreshToken())
        ) {
            throwUnauthorizedAccessException(Long.parseLong(memberId));
        }
    }

    private void throwUnauthorizedAccessException(Long memberId) {
        tokenService.deleteToken(memberId);
        throw new UnauthorizedAccessException();
    }

    /***
     * @param memberId // 토큰을 초기화 하기 위한 메서드
     * @return
     */
    public String[] refreshTokens(Long memberId) {
        String accessToken = jwtTokenProvider.createAccessToken(memberId);

        return new String[] {accessToken};
    }
}