package cat.community.nyangmunity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
public class NyangMunityApplication {

	public static void main(String[] args) {
		SpringApplication.run(NyangMunityApplication.class, args);
	}

}
