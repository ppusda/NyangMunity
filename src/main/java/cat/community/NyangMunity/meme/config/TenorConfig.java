package cat.community.NyangMunity.meme.config;

import lombok.Getter;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties("tenor")
public class TenorConfig {

    private final String secretKey;
    private final String clientKey;

}
