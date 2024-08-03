package cat.community.NyangMunity.meme.batch.scheduler;

import cat.community.NyangMunity.meme.config.MemeBatchConfig;
import cat.community.NyangMunity.meme.config.MemeConfig;
import cat.community.NyangMunity.meme.service.MemeBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemeBatchScheduler {

    private final MemeBatchService memeBatchService;

    @Scheduled(cron = "0 0 4 * * ?") // 매 새벽마다 Job 실행, cron 표현식 (초 분 시 일 월 요일)
    public void memeBatchUpdateSchedule() {
        memeBatchService.runMemeBatchJob();
    }
}
