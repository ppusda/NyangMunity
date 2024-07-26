package cat.community.NyangMunity.meme.batch.reader;

import cat.community.NyangMunity.global.exception.MemeBatchReadException;
import cat.community.NyangMunity.meme.response.TenorApiResponse;
import cat.community.NyangMunity.meme.response.TenorResponse;
import cat.community.NyangMunity.meme.service.MemeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemeBatchReader implements ItemReader<List<TenorResponse>> {

    private final MemeService memeService;
    private boolean isRead = false; // 데이터가 이미 읽혔는지 여부를 저장하는 플래그

    @Override
    public List<TenorResponse> read() {
        if (!isRead) { // 아직 데이터를 읽지 않은 경우에만 데이터를 읽음
            try {
                Mono<List<TenorResponse>> tenorResponseListMono = memeService.getCatMeme();
                List<TenorResponse> tenorResponseList = tenorResponseListMono.block(); // block()을 사용하여 결과를 동기적으로 얻음

                log.info("TEST: READ 실행 !!!");
                isRead = true; // 데이터를 읽었음을 표시
                return tenorResponseList;
            } catch (Exception e) {
                log.error("ERROR : MemeBatchReader - {}", e.getMessage());
                throw new MemeBatchReadException();
            }
        } else {
            return null; // 이미 데이터를 읽은 경우 null을 반환하여 처리 종료를 알림
        }
    }

}
