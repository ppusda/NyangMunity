package cat.community.nyangmunity.global.exception.global;

import cat.community.nyangmunity.global.exception.NyangmunityException;
import lombok.Getter;

@Getter
public class ForbiddenException extends NyangmunityException {

	private static final String MESSAGE = "잘못된 접근입니다.";

	public ForbiddenException() {
		super(MESSAGE);
	}

	@Override
	public int getStatusCode() {
		return 403;
	}
}
