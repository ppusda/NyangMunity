package cat.community.nyangmunity;

import cat.community.nyangmunity.global.config.AppConfig;
import cat.community.nyangmunity.image.config.TenorConfig;
import cat.community.nyangmunity.member.provider.KakaoAuthProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties({AppConfig.class, TenorConfig.class, KakaoAuthProvider.class})
public class NyangMunityApplication {

	public static void main(String[] args) {
		SpringApplication.run(NyangMunityApplication.class, args);
	}

}
