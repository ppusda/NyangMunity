package cat.community.NyangMunity.image.request;

import lombok.Builder;

@Builder
public record UploadUrlRequest (
        String filename
) {

}
