package cat.community.NyangMunity.global.exception;

import lombok.Getter;

@Getter
public class MemeBatchReadException extends NyangmunityException{

    private static final String MESSAGE = "Batch Service Error - 이미지를 가져올 수 없습니다.";

    public MemeBatchReadException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 401;
    }
}
