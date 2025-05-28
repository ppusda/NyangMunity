package cat.community.nyangmunity.postImage.image.batch.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cat.community.nyangmunity.postImage.image.batch.service.ImageBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageBatchScheduler {

	private final ImageBatchService imageBatchService;

	@Scheduled(cron = "0 0 4 * * ?") // 매 새벽마다 Job 실행, cron 표현식 (초 분 시 일 월 요일)
	public void imageBatchUpdateSchedule() {
		imageBatchService.runImageBatch();
	}
}
