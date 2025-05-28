package cat.community.nyangmunity.postImage.image.util;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;

import cat.community.nyangmunity.postImage.image.config.S3Config;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Component
@RequiredArgsConstructor
public class ImageUtil {

	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;

	@Value("${cloud.aws.s3.path}")
	private String filePath;

	@Value("${nyangmunity.image-url}")
	private String imageUrl;

	private final AmazonS3 amazonS3;
	private final S3Config s3Config;

	public URL generatePresignedUrl(String filePath) {
		LocalDateTime expiration = LocalDateTime.now().plusMinutes(15);
		Date from = Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant());
		return amazonS3.generatePresignedUrl(bucketName, filePath, from, HttpMethod.PUT);
	}

	public String generateRandomUUID() {
		return UUID.randomUUID().toString();
	}

	public String createFilepath(String uuid, String fileName) {
		return uuid + fileName.substring(fileName.lastIndexOf('.'));
	}

	public String getImageUrl() {
		return imageUrl + filePath;
	}
}
