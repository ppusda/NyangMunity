package cat.community.nyangmunity.postImage.image.batch.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
