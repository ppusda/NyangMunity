package cat.community.nyangmunity.global.filter;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import cat.community.nyangmunity.global.data.JwtValidateStatus;
import cat.community.nyangmunity.global.exception.global.InternalServerErrorException;
import cat.community.nyangmunity.global.exception.global.UnauthorizedException;
import cat.community.nyangmunity.global.provider.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = getTokenFromRequest(request, "accessToken");

        if (!StringUtils.hasText(accessToken)) { // AccessToken 이 존재하지 않을 때
            filterChain.doFilter(request, response);
            return;
        }

        if (StringUtils.hasText(accessToken)) {
            JwtValidateStatus validateStatus = jwtTokenProvider.validateToken(accessToken);

            switch (validateStatus) {
                case DENIED -> {
                    SecurityContextHolder.clearContext();
                    throw new UnauthorizedException("올바르지 않은 토큰입니다.");
                }
                case EXPIRED -> {
                    SecurityContextHolder.clearContext();
                    throw new UnauthorizedException("토큰이 만료되었습니다.");
                }
                case ACCEPTED -> {
                    Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
                default -> throw new InternalServerErrorException();
            }
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private static String getTokenFromRequest(HttpServletRequest request, String tokenName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(tokenName)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}