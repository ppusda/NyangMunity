package cat.community.NyangMunity.image.service;

import cat.community.NyangMunity.image.entity.Image;
import cat.community.NyangMunity.image.repository.ImageRepository;
import cat.community.NyangMunity.image.response.UploadImageResponse;
import cat.community.NyangMunity.image.util.S3ImageUtil;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final S3ImageUtil s3ImageUtil;

    public UploadImageResponse createImageInfo(String filename) {
        String filePath = generateRandomUUIDFilePath(filename);
        Image image = Image.builder()
                .name(filename)
                .path(filePath)
                .board(null)
                .build();
        imageRepository.save(image);
        return generateURL(filePath);
    }

    private String generateRandomUUIDFilePath(String filename) {
        return UUID.randomUUID() + getExtension(filename);
    }

    private UploadImageResponse generateURL(String filePath) {
        return UploadImageResponse.from(s3ImageUtil.generatePresignedUrl(filePath), filePath);
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.'));
    }
}
