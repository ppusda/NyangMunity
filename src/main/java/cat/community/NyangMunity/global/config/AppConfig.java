package cat.community.NyangMunity.global.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Base64;

@Getter
@ConfigurationProperties(prefix = "nyangmunity")
public class AppConfig {

    public byte[] jwtKey;
    public String imagePath;
    public String domain;

    public void setJwtKey(String jwtKey) {
        this.jwtKey = Base64.getDecoder().decode(jwtKey);
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
