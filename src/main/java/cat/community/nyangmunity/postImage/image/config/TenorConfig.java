package cat.community.nyangmunity.postImage.image.config;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties("tenor")
public class TenorConfig {

	private final String secretKey;
	private final String clientKey;

	private static final String REQUEST_URL = "https://tenor.googleapis.com/v2/search";
	private static final String FILTER = "gif";
	private static final Integer LIMIT = 50;

	public URI generateRequestUri(String searchTerm) {
		return UriComponentsBuilder.fromUriString(REQUEST_URL)
			.queryParam("q", searchTerm)
			.queryParam("key", secretKey)
			.queryParam("client_key", clientKey)
			.queryParam("limit", LIMIT)
			.queryParam("media_filter", FILTER)
			.build(true)
			.toUri();
	}

}
