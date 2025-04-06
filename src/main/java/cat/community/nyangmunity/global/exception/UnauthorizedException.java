package cat.community.nyangmunity.global.exception;

import lombok.Getter;

@Getter
public class UnauthorizedException extends NyangmunityException{

    private static final String MESSAGE = "인증되지 않은 접근입니다.";

    public UnauthorizedException() {
        super(MESSAGE);
    }

    public UnauthorizedException(final String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 401;
    }
}
