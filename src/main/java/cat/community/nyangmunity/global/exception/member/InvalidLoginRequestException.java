package cat.community.nyangmunity.global.exception.member;

import cat.community.nyangmunity.global.exception.NyangmunityException;

public class InvalidLoginRequestException extends NyangmunityException {

	private static final String MESSAGE = "아이디/비밀번호가 올바르지 않습니다.";

	public InvalidLoginRequestException() {
		super(MESSAGE);
	}

	@Override
	public int getStatusCode() {
		return 400;
	}
}
