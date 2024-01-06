package cat.community.NyangMunity.token.util;

import cat.community.NyangMunity.global.config.JwtTokenProvider;
import cat.community.NyangMunity.user.entity.User;
import cat.community.NyangMunity.token.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@RequiredArgsConstructor
public class TokenRefresher {

    private final TokenRepository tokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void removeRefreshToken(User user) {
        tokenRepository.deleteByUserId(user.getId());
    }

    @Transactional
    public void addRefreshToken(User user) {
        user.addToken(jwtTokenProvider.createRefreshToken(user.getId()));
    }

}
