package cat.community.NyangMunity.global.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Base64;

@Getter
@ConfigurationProperties(prefix = "nyangmunity")
public class AppConfig {

    private final byte[] jwtKey;
    private final String imagePath;
    private final String domain;

    public AppConfig(String jwtKey, String imagePath, String domain) {
        this.jwtKey = Base64.getDecoder().decode(jwtKey);
        this.imagePath = imagePath;
        this.domain = domain;
    }
}
