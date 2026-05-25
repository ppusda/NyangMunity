package cat.community.nyangmunity.global.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import cat.community.nyangmunity.global.data.JwtValidateStatus;
import cat.community.nyangmunity.global.exception.NyangmunityException;
import cat.community.nyangmunity.global.exception.global.InternalServerErrorException;
import cat.community.nyangmunity.global.exception.member.AccessTokenExpiredException;
import cat.community.nyangmunity.global.exception.member.AccessTokenInvalidException;
import cat.community.nyangmunity.global.provider.JwtTokenProvider;
import cat.community.nyangmunity.member.redisRepository.AccessTokenBlacklistRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final AccessTokenBlacklistRepository accessTokenBlacklistRepository;
	private final HandlerExceptionResolver resolver;

	public JwtAuthenticationFilter(
		JwtTokenProvider jwtTokenProvider,
		AccessTokenBlacklistRepository accessTokenBlacklistRepository,
		@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver
	) {
		this.jwtTokenProvider = jwtTokenProvider;
		this.accessTokenBlacklistRepository = accessTokenBlacklistRepository;
		this.resolver = resolver;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		String accessToken = getTokenFromRequest(request);

		if (!StringUtils.hasText(accessToken)) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			authenticate(accessToken);
		} catch (NyangmunityException e) {
			// Filter 단계에서 던진 예외는 @RestControllerAdvice 로 도달하지 않으므로 직접 위임한다.
			// 위임 후에는 chain 진행을 중단해야 응답이 중복 작성되지 않는다.
			SecurityContextHolder.clearContext();
			resolver.resolveException(request, response, null, e);
			return;
		}

		filterChain.doFilter(request, response);
	}

	private void authenticate(String accessToken) {
		JwtValidateStatus status = jwtTokenProvider.validateToken(accessToken);
		switch (status) {
			case ACCEPTED -> {
				if (accessTokenBlacklistRepository.isBlacklisted(jwtTokenProvider.getJti(accessToken))) {
					throw new AccessTokenInvalidException();
				}
				Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
			case EXPIRED -> throw new AccessTokenExpiredException();
			case DENIED -> throw new AccessTokenInvalidException();
			default -> throw new InternalServerErrorException();
		}
	}

	private static String getTokenFromRequest(HttpServletRequest request) {
		String header = request.getHeader("Authorization");
		if (header != null && header.startsWith("Bearer ")) {
			return header.substring(7);
		}
		return null;
	}

}
