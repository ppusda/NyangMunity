package cat.community.NyangMunity.exception;

public class EmptyInputValueException extends NyangmunityException{
    private static final String MESSAGE = "필수 항목을 입력해주시길 바랍니다.";

    private String fieldName;
    private String message;

    public EmptyInputValueException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
