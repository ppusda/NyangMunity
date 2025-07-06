package cat.community.nyangmunity.global.response;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;

public record ErrorResponse(
	String code,
	String message,
	Map<String, String> validation
) {
	@Builder
	public ErrorResponse {
	}

	public void addValidation(String fieldName, String errorMessage) {
		this.validation.put(fieldName, errorMessage);
	}
}
