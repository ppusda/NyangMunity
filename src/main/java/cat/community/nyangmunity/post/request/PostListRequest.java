package cat.community.nyangmunity.post.request;

import lombok.Builder;
import lombok.Getter;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Getter @Builder
public class PostListRequest {

    private static final Integer MAX_SIZE = 100;

    @Builder.Default
    private Integer page = 1;

    @Builder.Default
    private Integer size = 10;

    public long getOffset() {
        return (long) (max(1, page) - 1) * min(MAX_SIZE, size);
    }
}
