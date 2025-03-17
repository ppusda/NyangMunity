package cat.community.nyangmunity.image.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Getter
@Configuration
public class ImageConfig {

	@Value("${nyangmunity.image-url}")
	private String imageUrl;
}
