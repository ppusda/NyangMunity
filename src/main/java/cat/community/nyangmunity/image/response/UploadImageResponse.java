package cat.community.nyangmunity.image.response;

import java.net.URL;
import lombok.Builder;

@Builder
public record UploadImageResponse(
        String id,
        URL uploadUrl
) {

    public static UploadImageResponse from(URL url, String imageId) {
        return UploadImageResponse.builder()
                .id(imageId)
                .uploadUrl(url)
                .build();
    }

}
