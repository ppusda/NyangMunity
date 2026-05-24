package cat.community.nyangmunity.global.exception.member;

import cat.community.nyangmunity.global.exception.NyangmunityException;

/**
 * Access 토큰이 만료된 경우. 프론트는 이 코드를 받으면 /tokens 로 재발급을 시도한다.
 */
public class AccessTokenExpiredException extends NyangmunityException {

	private static final String MESSAGE = "토큰이 만료되었습니다.";
	private static final String ERROR_CODE = "TOKEN_EXPIRED";

	public AccessTokenExpiredException() {
		super(MESSAGE);
	}

	@Override
	public int getStatusCode() {
		return 401;
	}

	@Override
	public String getErrorCode() {
		return ERROR_CODE;
	}
}
