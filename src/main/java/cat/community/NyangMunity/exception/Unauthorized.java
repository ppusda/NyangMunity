package cat.community.NyangMunity.exception;

import lombok.Getter;

@Getter
public class Unauthorized extends NyangmunityException{

    private static final String MESSAGE = "인증이 필요합니다..";

    public Unauthorized() {
        super(MESSAGE);
    }

    public Unauthorized(String fieldName, String message) {
        super(MESSAGE);
        addValidation(fieldName, message);
    }

    @Override
    public int statusCode() {
        return 401;
    }
}
