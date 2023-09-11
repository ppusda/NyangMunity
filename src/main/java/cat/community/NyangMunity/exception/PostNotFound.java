package cat.community.NyangMunity.exception;

import lombok.Getter;

@Getter
public class PostNotFound extends NyangmunityException{

    private static final String MESSAGE = "존재하지 않는 글입니다.";

    private String fieldName;
    private String message;

    public PostNotFound() {
        super(MESSAGE);
    }

    public PostNotFound(String fieldName, String message) {
        super(MESSAGE);
        this.fieldName = fieldName;
        this.message = message;
    }

    @Override
    public int statusCode() {
        return 404;
    }
}
