package cat.community.NyangMunity.global.exception;

import lombok.Getter;

@Getter
public class UnauthorizedUserException extends NyangmunityException{

    private static final String MESSAGE = "인증되지 않은 유저입니다.";

    public UnauthorizedUserException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 401;
    }
}
