package cat.community.NyangMunity.config;

import cat.community.NyangMunity.domain.Token;
import cat.community.NyangMunity.request.UserSession;
import cat.community.NyangMunity.exception.Unauthorized;
import cat.community.NyangMunity.repository.TokenRepository;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class AuthResolver implements HandlerMethodArgumentResolver {

    private final TokenRepository tokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(UserSession.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        if (servletRequest == null) {
            log.error(">>> ServletRequest Error");
            throw new Unauthorized();
        }

        Cookie[] cookies = servletRequest.getCookies();
        if(cookies.length == 0){
            log.error(">>> No Have AccessToken");
            throw new Unauthorized();
        }

        String accessToken = cookies[0].getValue();

        try {
            if(jwtTokenProvider.validateToken(accessToken)){
                String userId = jwtTokenProvider.getClaims(accessToken).getBody().getSubject();
                return new UserSession(Long.parseLong(userId), accessToken);
            } else{
                String userId = jwtTokenProvider.getClaims(accessToken).getBody().getSubject();
                Optional<Token> token = tokenRepository.findByUser_Id(Long.parseLong(userId));

                String refreshToken = token.get().getRefreshToken();
                if(jwtTokenProvider.validateToken(refreshToken)) {
                    String newAccessToken = jwtTokenProvider.createAccessToken(Long.parseLong(userId));
                    return new UserSession(Long.parseLong(userId), newAccessToken);
                }else {
                    throw new Unauthorized();
                }
            }

        } catch (JwtException e) {
            throw new Unauthorized();
        }
    }
}

/*** 쿠키를 이용한 인증 방법
 *         return new UserSession(session.getUser().getId());
 */