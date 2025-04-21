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

	public void addValidation(String filedName, String message) {
		validation.put(filedName, message);
	}
}
