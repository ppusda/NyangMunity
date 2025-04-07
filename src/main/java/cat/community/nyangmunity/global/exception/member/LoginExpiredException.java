package cat.community.nyangmunity.global.exception.member;

import cat.community.nyangmunity.global.exception.NyangmunityException;
import lombok.Getter;

@Getter
public class LoginExpiredException extends NyangmunityException {

    private static final String MESSAGE = "재로그인이 필요합니다.";

    public LoginExpiredException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
