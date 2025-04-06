package cat.community.nyangmunity.global.exception.batch;

import cat.community.nyangmunity.global.exception.NyangmunityException;
import lombok.Getter;

@Getter
public class ImageBatchReadException extends NyangmunityException {

    private static final String MESSAGE = "Batch Service Error - 이미지를 가져올 수 없습니다.";

    public ImageBatchReadException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 500;
    }
}
