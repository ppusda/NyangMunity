package cat.community.NyangMunity;

import cat.community.NyangMunity.global.config.AppConfig;
import cat.community.NyangMunity.meme.config.TenorConfig;
import cat.community.NyangMunity.user.provider.KakaoAuthProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({AppConfig.class, TenorConfig.class, KakaoAuthProvider.class})
public class NyangMunityApplication {

	public static void main(String[] args) {
		SpringApplication.run(NyangMunityApplication.class, args);
	}

}
