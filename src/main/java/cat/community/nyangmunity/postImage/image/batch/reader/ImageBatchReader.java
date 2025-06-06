package cat.community.nyangmunity.postImage.image.batch.reader;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import cat.community.nyangmunity.global.exception.batch.ImageBatchReadException;
import cat.community.nyangmunity.postImage.image.batch.response.TenorResponse;
import cat.community.nyangmunity.postImage.image.batch.service.ImageApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageBatchReader implements ItemReader<List<TenorResponse>> {

	private boolean isRead = false; // 데이터가 이미 읽혔는지 여부를 저장하는 플래그
	private static final String[] searchTerms = {"cat", "cuteCat"}; // 검색어

	private final ImageApiService imageApiService;

	@Override
	public List<TenorResponse> read() {
		List<TenorResponse> tenorResponseList = new ArrayList<>();

		if (!isRead) { // 아직 데이터를 읽지 않은 경우에만 데이터를 읽음
			try {
				List<Mono<List<TenorResponse>>> monoList = new ArrayList<>();
				for (String searchTerm : searchTerms) {
					monoList.add(imageApiService.getCatImages(searchTerm));
				}

				List<List<TenorResponse>> resultList = Flux.merge(monoList)
					.collectList()
					.block();

				for (List<TenorResponse> responses : resultList) {
					tenorResponseList.addAll(responses);
				}

				isRead = true;
				return tenorResponseList;
			} catch (Exception e) {
				log.error("ERROR : ImageBatchReader - {}", e.getMessage());
				throw new ImageBatchReadException();
			}
		} else {
			return null; // 이미 데이터를 읽은 경우 null을 반환하여 처리 종료를 알림
		}
	}

}
