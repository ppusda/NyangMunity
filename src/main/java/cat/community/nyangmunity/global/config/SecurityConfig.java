package cat.community.nyangmunity.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import cat.community.nyangmunity.global.exception.CustomAccessDeniedHandler;
import cat.community.nyangmunity.global.exception.CustomAuthenticationEntryPoint;
import cat.community.nyangmunity.global.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final CustomAuthenticationEntryPoint authenticationEntryPoint;
	private final CustomAccessDeniedHandler accessDeniedHandler;

	@Bean
	SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
		http
			.exceptionHandling(exception -> exception
				.accessDeniedHandler(accessDeniedHandler)
				.authenticationEntryPoint(authenticationEntryPoint)
			)
			.csrf(
				AbstractHttpConfigurer::disable
			)
			.cors(
				cors -> cors.configure(http)
			)
			.headers(
				headers -> headers.frameOptions(FrameOptionsConfig::disable)
			)
			.authorizeHttpRequests(requests ->
				requests
					.requestMatchers( // Posts - GET
						HttpMethod.GET,
						"/posts",
						"/posts/likes"
					).permitAll()
					.requestMatchers( // Images - GET
						HttpMethod.GET,
						"/images",
						"/images/providers"
					).permitAll()
					.requestMatchers( // Members - GET
						HttpMethod.GET,
						"/members/check"
					).permitAll()
					.requestMatchers( // Members - POST
						HttpMethod.POST,
						"/members/join",
						"/members/login",
						"/members/logout"
					).permitAll()
					.requestMatchers( // Tokens - POST
						HttpMethod.POST,
						"/tokens"
					).permitAll()

					.anyRequest().authenticated()
			)
			.sessionManagement(sessionManagement ->
				sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}