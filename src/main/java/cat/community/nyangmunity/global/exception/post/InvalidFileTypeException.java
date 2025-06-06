package cat.community.nyangmunity.global.exception.post;

import cat.community.nyangmunity.global.exception.NyangmunityException;

public class InvalidFileTypeException extends NyangmunityException {
	private static final String MESSAGE = "지원되지 않는 파일 형식입니다.";

	public InvalidFileTypeException() {
		super(MESSAGE);
	}

	@Override
	public int getStatusCode() {
		return 400;
	}
}
