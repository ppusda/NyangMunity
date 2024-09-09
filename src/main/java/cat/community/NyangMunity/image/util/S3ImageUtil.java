package cat.community.NyangMunity.image.util;

import cat.community.NyangMunity.image.config.S3Config;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
@RequiredArgsConstructor
public class S3ImageUtil {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.s3.path}")
    private String filePath;

    private final AmazonS3 amazonS3;
    private final S3Config s3Config;

    public URL generatePresignedUrl(String filePath) {
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(15);

        Date from = Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant());
        return amazonS3.generatePresignedUrl(bucketName, filePath, from, HttpMethod.PUT);
    }

}
