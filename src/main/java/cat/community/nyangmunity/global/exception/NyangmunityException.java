package cat.community.nyangmunity.global.exception;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public abstract class NyangmunityException extends RuntimeException {
	public final Map<String, String> validation = new HashMap<>();

	public NyangmunityException(String message) {
		super(message);
	}

	public NyangmunityException(String message, Throwable cause) {
		super(message, cause);
	}

	public abstract int getStatusCode();

	/**
	 * 응답 body 의 의미적 에러 코드. 클라이언트가 HTTP status 만으로는 분기하기 어려운 경우
	 * (예: 401 안에서 TOKEN_EXPIRED vs TOKEN_INVALID) 에 사용한다. null 이면 ExceptionController 가
	 * statusCode 문자열로 폴백한다.
	 */
	public String getErrorCode() {
		return null;
	}

	public void addValidation(String filedName, String message) {
		validation.put(filedName, message);
	}
}
