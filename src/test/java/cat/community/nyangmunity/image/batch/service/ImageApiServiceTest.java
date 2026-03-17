package cat.community.nyangmunity.image.batch.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import cat.community.nyangmunity.postImage.image.batch.response.TenorResponse;
import cat.community.nyangmunity.postImage.image.batch.service.ImageApiService;

@SpringBootTest
class ImageApiServiceTest {

	@Autowired
	private ImageApiService imageApiService;

	@Test
	@DisplayName("Tenor API 키가 유효하고 결과를 반환한다")
	void tenorApiKeyIsValid() {
		List<TenorResponse> results = imageApiService.getCatImages("cat").block();

		assertThat(results).isNotNull();
		assertThat(results).isNotEmpty();
	}

	@Test
	@DisplayName("Tenor API 응답에 gif URL이 포함된다")
	void tenorResponseContainsGifUrl() {
		List<TenorResponse> results = imageApiService.getCatImages("cat").block();

		assertThat(results).isNotNull();
		TenorResponse first = results.get(0);
		assertThat(first.id()).isNotBlank();
		assertThat(first.media_formats()).isNotNull();
		assertThat(first.media_formats().gif()).isNotNull();
		assertThat(first.media_formats().gif().url()).isNotBlank();
	}

	@Test
	@DisplayName("Tenor API 응답에 tinygif URL이 포함된다")
	void tenorResponseContainsTinygifUrl() {
		List<TenorResponse> results = imageApiService.getCatImages("cat").block();

		assertThat(results).isNotNull();
		TenorResponse first = results.get(0);
		assertThat(first.media_formats().tinygif()).isNotNull();
		assertThat(first.media_formats().tinygif().url()).isNotBlank();
	}
}
