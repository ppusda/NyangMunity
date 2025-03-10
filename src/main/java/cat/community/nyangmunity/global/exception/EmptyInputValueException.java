package cat.community.nyangmunity.global.exception;

public class EmptyInputValueException extends NyangmunityException{
    private static final String MESSAGE = "필수 항목을 입력해주시길 바랍니다.";

    public EmptyInputValueException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
