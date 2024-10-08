package cat.community.NyangMunity.image.response;

import java.net.URL;
import lombok.Builder;

@Builder
public record UploadImageResponse(
        URL uploadUrl,
        String filePath,
        Long imgId

) {

    public static UploadImageResponse from(URL url, String filePath, Long imgId) {
        return UploadImageResponse.builder()
                .uploadUrl(url)
                .filePath(filePath)
                .imgId(imgId)
                .build();
    }

}
