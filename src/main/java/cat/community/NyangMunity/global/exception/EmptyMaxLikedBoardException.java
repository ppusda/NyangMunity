package cat.community.NyangMunity.global.exception;

public class EmptyMaxLikedBoardException extends NyangmunityException{
    private static final String MESSAGE = "좋아요가 있는 글이 없습니다.";

    public EmptyMaxLikedBoardException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
