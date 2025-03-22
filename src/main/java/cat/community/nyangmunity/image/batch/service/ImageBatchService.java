package cat.community.nyangmunity.image.batch.service;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import cat.community.nyangmunity.image.batch.response.TenorApiResponse;
import cat.community.nyangmunity.image.batch.response.TenorResponse;
import cat.community.nyangmunity.image.config.TenorConfig;
import io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageBatchService {

    private final JobLauncher jobLauncher;
    private final Job imageBatchJob;

    public void runImageBatch() {
        try {
            String execDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            JobParameters parameters = new JobParametersBuilder()
                    .addString("execDate", execDate)
                    .toJobParameters();

            jobLauncher.run(imageBatchJob, parameters);
        } catch (Exception e) {
            log.error("[ERROR] : 배치 업데이트 중 오류 발생, {}", e.getMessage());
        }
    }

}
