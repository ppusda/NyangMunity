package cat.community.nyangmunity.image.response;

import java.net.URL;
import lombok.Builder;

@Builder
public record UploadImageResponse(
        String id,
        URL uploadUrl,
        String filePath

) {

    public static UploadImageResponse from(URL url, String filePath, String imageId) {
        return UploadImageResponse.builder()
                .id(imageId)
                .uploadUrl(url)
                .filePath(filePath)
                .build();
    }

}
