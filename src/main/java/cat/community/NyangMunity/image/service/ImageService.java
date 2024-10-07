package cat.community.NyangMunity.image.service;

import cat.community.NyangMunity.image.entity.Image;
import cat.community.NyangMunity.image.repository.ImageRepository;
import cat.community.NyangMunity.image.response.UploadImageResponse;
import cat.community.NyangMunity.image.util.S3ImageUtil;
import java.net.URL;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final S3ImageUtil s3ImageUtil;

    /***
     * // 업로드 할 URL을 반환해주는 메서드
     * @param filename // 파일 이름을 전달 받으면 UUID 파일 이름으로 변환, 이를 DB에 저장
     * @return // 이후 이미지를 업로드 할 수 있는 presigned url, 변환된 이름의 filePath, 할당된 imageId를 반환한다.
     */
    public UploadImageResponse createImageInfo(String filename) {
        String filePath = generateRandomUUIDFilePath(filename);
        Image image = Image.builder()
                .name(filename)
                .path(filePath)
                .board(null)
                .build();

        Image savedImage = imageRepository.save(image);
        return UploadImageResponse.from(generateURL(filePath), filePath, savedImage.getId());
    }

    private String generateRandomUUIDFilePath(String filename) {
        return UUID.randomUUID() + getExtension(filename);
    }

    private URL generateURL(String filePath) {
        return s3ImageUtil.generatePresignedUrl(filePath);
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.'));
    }
}
