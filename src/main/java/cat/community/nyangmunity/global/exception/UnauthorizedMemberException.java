package cat.community.nyangmunity.global.exception;

import lombok.Getter;

@Getter
public class UnauthorizedMemberException extends NyangmunityException{

    private static final String MESSAGE = "인증되지 않은 유저입니다.";

    public UnauthorizedMemberException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 401;
    }
}
