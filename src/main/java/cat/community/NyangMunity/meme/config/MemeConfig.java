package cat.community.NyangMunity.meme.config;

import java.net.URI;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.util.UriComponentsBuilder;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties("tenor")
public class MemeConfig {

    private final String secretKey;
    private final String clientKey;

    private static final String REQUEST_URL = "https://tenor.googleapis.com/v2/search";
    private static final String SEARCH_TERM = "catmeme";
    private static final Integer LIMIT = 50;

    public URI generateRequestUri() {
        return UriComponentsBuilder.fromUriString(REQUEST_URL)
                .queryParam("q", SEARCH_TERM)
                .queryParam("key", secretKey)
                .queryParam("client_key", clientKey)
                .queryParam("limit", LIMIT)
                .build(true)
                .toUri();
    }

}
