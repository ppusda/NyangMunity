package cat.community.nyangmunity.global.exception;

import java.io.IOException;
import java.util.Map;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import cat.community.nyangmunity.global.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private static final String ERROR_CODE = "UNAUTHENTICATED";
	private static final String MESSAGE = "인증이 필요합니다.";

	private final ObjectMapper objectMapper;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException {

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");

		ErrorResponse body = ErrorResponse.builder()
			.code(ERROR_CODE)
			.message(MESSAGE)
			.validation(Map.of())
			.build();

		response.getWriter().write(objectMapper.writeValueAsString(body));
	}
}
