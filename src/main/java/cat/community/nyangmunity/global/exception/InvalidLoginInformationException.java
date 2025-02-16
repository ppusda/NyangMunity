package cat.community.nyangmunity.global.exception;

public class InvalidLoginInformationException extends NyangmunityException{

    private static final String MESSAGE = "아이디/비밀번호가 올바르지 않습니다.";

    public InvalidLoginInformationException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
