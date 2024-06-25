package cat.community.NyangMunity.global.exception;

public class InvalidPasswordException extends NyangmunityException{

    private static final String MESSAGE = "비밀번호를 확인해주십시오.";

    public InvalidPasswordException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
