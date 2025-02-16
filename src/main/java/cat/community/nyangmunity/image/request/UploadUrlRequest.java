package cat.community.nyangmunity.image.request;

import lombok.Builder;

@Builder
public record UploadUrlRequest (
        String filename
) {

}
