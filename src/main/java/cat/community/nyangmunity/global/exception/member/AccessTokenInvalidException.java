package cat.community.nyangmunity.global.exception.member;

import cat.community.nyangmunity.global.exception.NyangmunityException;

/**
 * Access 토큰 서명·구조가 잘못됐거나 블랙리스트에 등록된 경우. 프론트는 이 코드를 받으면
 * 재발급을 시도하지 않고 즉시 로컬 인증 상태를 정리한다.
 */
public class AccessTokenInvalidException extends NyangmunityException {

	private static final String MESSAGE = "올바르지 않은 토큰입니다.";
	private static final String ERROR_CODE = "TOKEN_INVALID";

	public AccessTokenInvalidException() {
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
