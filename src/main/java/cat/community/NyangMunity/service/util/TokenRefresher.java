package cat.community.NyangMunity.service.util;

import cat.community.NyangMunity.config.JwtTokenProvider;
import cat.community.NyangMunity.domain.User;
import cat.community.NyangMunity.repository.TokenRepository;
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
