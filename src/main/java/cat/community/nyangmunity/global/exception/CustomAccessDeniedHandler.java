package cat.community.nyangmunity.global.exception;

import java.io.IOException;
import java.util.Map;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import cat.community.nyangmunity.global.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	private static final String ERROR_CODE = "ACCESS_DENIED";
	private static final String MESSAGE = "권한이 없습니다.";

	private final ObjectMapper objectMapper;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		AccessDeniedException accessDeniedException) throws IOException {

		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
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
