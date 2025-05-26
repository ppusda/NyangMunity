package cat.community.nyangmunity.global.exception.global;

import cat.community.nyangmunity.global.exception.NyangmunityException;
import lombok.Getter;

@Getter
public class BadRequestException extends NyangmunityException {

	private static final String MESSAGE = "잘못된 요청입니다.";

	public BadRequestException() {
		super(MESSAGE);
	}

	public BadRequestException(final String message) {
		super(message);
	}

	@Override
	public int getStatusCode() {
		return 400;
	}
}
