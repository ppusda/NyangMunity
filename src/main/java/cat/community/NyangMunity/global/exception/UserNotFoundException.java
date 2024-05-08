package cat.community.NyangMunity.global.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends NyangmunityException{

    private static final String MESSAGE = "존재하지 않는 유저 입니다.";

    public UserNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
