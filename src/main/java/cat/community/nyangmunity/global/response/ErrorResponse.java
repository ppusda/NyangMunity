package cat.community.nyangmunity.global.response;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Getter
//@JsonInclude(value = JsonInclude.Include.NON_EMPTY) 이를 통해서 Null로 들어오는 값을 제외할 수 있다.
public class ErrorResponse {
	private final String code;
	private final String message;
	private final Map<String, String> validation;

	@Builder
	public ErrorResponse(String code, String message, Map<String, String> validation) {
		this.code = code;
		this.message = message;
		this.validation = validation;
	}

	public void addValidation(String fieldName, String errorMessage) {
		this.validation.put(fieldName, errorMessage);
	}
}
