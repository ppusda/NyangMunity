package cat.community.nyangmunity.global.exception;

public class AlreadyExistsNicknameException extends NyangmunityException{
    private static final String MESSAGE = "이미 존재하는 닉네임 입니다.";

    public AlreadyExistsNicknameException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
