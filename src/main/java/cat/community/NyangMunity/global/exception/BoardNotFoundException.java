package cat.community.NyangMunity.global.exception;

import lombok.Getter;

@Getter
public class BoardNotFoundException extends NyangmunityException{

    private static final String MESSAGE = "존재하지 않는 글입니다.";

    public BoardNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
