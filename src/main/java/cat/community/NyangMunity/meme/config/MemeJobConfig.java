package cat.community.NyangMunity.meme.config;

import cat.community.NyangMunity.meme.service.MemeService;
import cat.community.NyangMunity.meme.tasklet.getCatMemeTasklet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MemeJobConfig {

    private final MemeService memeService;

    @Bean(name = "memeJob")
    public Job memeJob(JobRepository jobRepository, Step simpleStep1) {
        return new JobBuilder("memeJob", jobRepository)
                .start(simpleStep1)
                .build();
    }
    @Bean
    public Step getCatMemeStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("getCatMeme", jobRepository)
                .tasklet(new getCatMemeTasklet(memeService), platformTransactionManager)
                .build();
    }
}
