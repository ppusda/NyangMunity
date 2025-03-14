package cat.community.nyangmunity.global.exception;

import lombok.Getter;

@Getter
public class UnauthorizedAccessException extends NyangmunityException{

    private static final String MESSAGE = "잘못된 접근입니다.";

    public UnauthorizedAccessException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 401;
    }
}
