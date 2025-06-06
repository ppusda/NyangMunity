package cat.community.nyangmunity.global.exception.member;

import cat.community.nyangmunity.global.exception.NyangmunityException;

public class InvalidPasswordException extends NyangmunityException {

	private static final String MESSAGE = "비밀번호를 확인해주십시오.";

	public InvalidPasswordException() {
		super(MESSAGE);
	}

	@Override
	public int getStatusCode() {
		return 400;
	}
}
