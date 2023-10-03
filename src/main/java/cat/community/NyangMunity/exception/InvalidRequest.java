package cat.community.NyangMunity.exception;

import lombok.Getter;

@Getter
public class InvalidRequest extends NyangmunityException{

    private static final String MESSAGE = "잘못된 요청입니다.";

    public InvalidRequest() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
