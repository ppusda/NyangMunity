package cat.community.nyangmunity.global.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cat.community.nyangmunity.global.exception.NyangmunityException;
import cat.community.nyangmunity.global.response.ErrorResponse;

@RestControllerAdvice
public class ExceptionController {

	@ResponseBody
	@ExceptionHandler(NyangmunityException.class)
	public ResponseEntity<ErrorResponse> globalResponseException(NyangmunityException e) {
		int statusCode = e.getStatusCode();

		ErrorResponse body = ErrorResponse.builder()
			.code(String.valueOf(statusCode))
			.message(e.getMessage())
			.validation(e.getValidation())
			.build();

		return ResponseEntity.status(statusCode).body(body);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error ->
			errors.put("message", error.getDefaultMessage())
		);
		return ResponseEntity.badRequest().body(errors);
	}
}
