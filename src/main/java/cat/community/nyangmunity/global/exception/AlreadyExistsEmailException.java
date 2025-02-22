package cat.community.nyangmunity.global.exception;

public class AlreadyExistsEmailException extends NyangmunityException{
    private static final String MESSAGE = "이미 가입된 이메일 입니다.";

    public AlreadyExistsEmailException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
