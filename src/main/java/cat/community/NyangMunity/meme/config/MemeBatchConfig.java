package cat.community.NyangMunity.meme.config;

import cat.community.NyangMunity.meme.batch.processor.MemeBatchProcessor;
import cat.community.NyangMunity.meme.batch.reader.MemeBatchReader;
import cat.community.NyangMunity.meme.entity.Meme;
import cat.community.NyangMunity.meme.response.TenorResponse;
import cat.community.NyangMunity.meme.service.MemeService;
import cat.community.NyangMunity.meme.batch.writer.MemeBatchWriter;
import java.util.List;
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
public class MemeBatchConfig {

    // TODO: 향후 이미지 개수 늘려 수집 진행하면 CHUNK 범위 조정하며 적정 값 잧기
    private final static int CHUNK_SIZE = 50;

    @Bean(name = "memeBatchJob")
    public Job memeJob(JobRepository jobRepository, Step step) {
        return new JobBuilder("memeBatchJob", jobRepository)
                .start(step)
                .build();
    }

    @Bean
    public Step getCatMemeStep(
            JobRepository jobRepository,
            PlatformTransactionManager platformTransactionManager,
            MemeBatchReader memeBatchReader,
            MemeBatchProcessor memeBatchProcessor,
            MemeBatchWriter memeBatchwriter
    ){
        return new StepBuilder("getCatMeme", jobRepository)
                .<List<TenorResponse>, List<Meme>>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(memeBatchReader)
                .processor(memeBatchProcessor)
                .writer(memeBatchwriter)
                .build();
    }
}
