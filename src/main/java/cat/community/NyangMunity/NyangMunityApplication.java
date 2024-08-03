package cat.community.NyangMunity;

import cat.community.NyangMunity.global.config.AppConfig;
import cat.community.NyangMunity.meme.config.MemeConfig;
import cat.community.NyangMunity.user.provider.KakaoAuthProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties({AppConfig.class, MemeConfig.class, KakaoAuthProvider.class})
public class NyangMunityApplication {

	public static void main(String[] args) {
		SpringApplication.run(NyangMunityApplication.class, args);
	}

}
