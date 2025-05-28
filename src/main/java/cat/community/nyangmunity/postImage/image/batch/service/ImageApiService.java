package cat.community.nyangmunity.postImage.image.batch.service;

import java.net.URI;
import java.util.List;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import cat.community.nyangmunity.postImage.image.batch.response.TenorApiResponse;
import cat.community.nyangmunity.postImage.image.batch.response.TenorResponse;
import cat.community.nyangmunity.postImage.image.config.TenorConfig;
import io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageApiService {

	private final TenorConfig tenorConfig;

	public Mono<List<TenorResponse>> getCatImages(String searchTerm) {
		URI requestUri = tenorConfig.generateRequestUri(searchTerm);

		HttpClient httpClient = HttpClient.create()
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);

		WebClient webClient = WebClient.builder()
			.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(5 * 1024 * 1024))
			.baseUrl(requestUri.toString())
			.clientConnector(new ReactorClientHttpConnector(httpClient))
			.build();

		return webClient.get()
			.uri(requestUri)
			.retrieve()
			.bodyToMono(TenorApiResponse.class)
			.map(TenorApiResponse::results)
			.doOnError(error -> {
				log.error("ERROR : getCatImage() - {}", error.getMessage());
			});
	}
}
