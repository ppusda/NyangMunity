package cat.community.NyangMunity.config;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.util.Base64;

@ConfigurationProperties(prefix = "nyangmunity")
public class AppConfig {

    public byte[] jwtKey; // .yml 파일 작성에 따라 다양하게 사용가능 String, List, Map, Object...
    public String imagePath;
    public String domain;

    public void setJwtKey(String jwtKey) {
        this.jwtKey = Base64.getDecoder().decode(jwtKey);
    }

    public byte[] getJwtKey() {
        return jwtKey;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("utf-8");
        resolver.setMaxUploadSizePerFile(10 * 1024 * 1024);
        return resolver;
    }
}
