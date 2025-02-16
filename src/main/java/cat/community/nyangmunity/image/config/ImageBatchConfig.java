package cat.community.nyangmunity.image.config;

import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import cat.community.nyangmunity.image.batch.processor.ImageBatchProcessor;
import cat.community.nyangmunity.image.batch.reader.ImageBatchReader;
import cat.community.nyangmunity.image.batch.response.TenorResponse;
import cat.community.nyangmunity.image.batch.writer.ImageBatchWriter;
import cat.community.nyangmunity.image.entity.Image;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ImageBatchConfig {

    // TODO: 향후 이미지 개수 늘려 수집 진행하면 CHUNK 범위 조정하며 적정 값 잧기
    private final static int CHUNK_SIZE = 50;

    @Bean(name = "imageBatchJob")
    public Job memeJob(JobRepository jobRepository, Step step) {
        return new JobBuilder("imageBatchJob", jobRepository)
                .start(step)
                .build();
    }

    @Bean
    public Step getCatImageStep(
            JobRepository jobRepository,
            PlatformTransactionManager platformTransactionManager,
            ImageBatchReader imageBatchReader,
            ImageBatchProcessor imageBatchProcessor,
            ImageBatchWriter imageBatchwriter
    ){
        return new StepBuilder("getCatMeme", jobRepository)
                .<List<TenorResponse>, List<Image>>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(imageBatchReader)
                .processor(imageBatchProcessor)
                .writer(imageBatchwriter)
                .build();
    }
}
