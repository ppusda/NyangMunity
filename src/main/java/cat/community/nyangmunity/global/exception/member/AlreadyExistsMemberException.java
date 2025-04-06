package cat.community.nyangmunity.global.exception.member;

import cat.community.nyangmunity.global.exception.NyangmunityException;

public class AlreadyExistsMemberException extends NyangmunityException {
    private static final String MESSAGE = "이미 가입된 이메일 입니다.";

    public AlreadyExistsMemberException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
