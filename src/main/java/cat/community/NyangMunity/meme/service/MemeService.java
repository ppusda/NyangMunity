package cat.community.NyangMunity.meme.service;

import cat.community.NyangMunity.meme.config.MemeConfig;
import cat.community.NyangMunity.meme.entity.Meme;
import cat.community.NyangMunity.meme.repository.MemeRepository;
import cat.community.NyangMunity.meme.response.TenorApiResponse;
import cat.community.NyangMunity.meme.response.TenorResponse;
import io.netty.channel.ChannelOption;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemeService {

    private final MemeConfig memeConfig;
    private final MemeRepository memeRepository;

    public Mono<List<TenorResponse>> getCatMeme(String searchTerm) {
        URI requestUri = memeConfig.generateRequestUri(searchTerm);

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
                   log.error("ERROR : getCatMeme() - {}", error.getMessage());
                });
    }

    @Transactional(readOnly = true)
    public List<Meme> getAllMemes() {
        return memeRepository.findAll();
    }

    @Transactional
    public void saveMemes(List<Meme> saveMemeList) {
        memeRepository.saveAll(saveMemeList);
    }

    @Transactional
    public void deleteMemes(List<Meme> deleteMemeList) {
        memeRepository.deleteAll(deleteMemeList);
    }
}
