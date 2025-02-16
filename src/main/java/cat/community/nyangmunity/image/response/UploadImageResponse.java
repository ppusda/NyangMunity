package cat.community.nyangmunity.image.response;

import java.net.URL;
import lombok.Builder;

@Builder
public record UploadImageResponse(
        URL uploadUrl,
        String filePath,
        String imgId

) {

    public static UploadImageResponse from(URL url, String filePath, String imageId) {
        return UploadImageResponse.builder()
                .uploadUrl(url)
                .filePath(filePath)
                .imgId(imageId)
                .build();
    }

}
