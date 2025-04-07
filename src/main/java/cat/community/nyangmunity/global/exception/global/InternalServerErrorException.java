package cat.community.nyangmunity.global.exception.global;

import cat.community.nyangmunity.global.exception.NyangmunityException;
import lombok.Getter;

@Getter
public class InternalServerErrorException extends NyangmunityException {

    private static final String MESSAGE = "예기치 못한 오류가 발생했습니다.";

    public InternalServerErrorException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 500;
    }
}
