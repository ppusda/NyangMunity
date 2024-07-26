package cat.community.NyangMunity.meme.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemeBatchService {

    private final JobLauncher jobLauncher;
    private final Job memeBatchJob;

    public void runMemeBatchJob() {
        try {
            String execDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            JobParameters parameters = new JobParametersBuilder()
                    .addString("execDate", execDate)
                    .toJobParameters();

            jobLauncher.run(memeBatchJob, parameters);
        } catch (Exception e) {
            log.error("[ERROR] : 배치 업데이트 중 오류 발생, {}", e.getMessage());
        }
    }

}
