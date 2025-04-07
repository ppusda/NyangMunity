package cat.community.nyangmunity.global.exception.global;

import cat.community.nyangmunity.global.exception.NyangmunityException;
import lombok.Getter;

@Getter
public class InvalidRequestException extends NyangmunityException {

    private static final String MESSAGE = "잘못된 요청입니다.";

    public InvalidRequestException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
