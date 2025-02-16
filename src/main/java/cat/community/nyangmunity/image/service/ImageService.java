package cat.community.nyangmunity.image.service;

import cat.community.nyangmunity.image.batch.response.ImageResponse;
import cat.community.nyangmunity.image.batch.response.TenorApiResponse;
import cat.community.nyangmunity.image.batch.response.TenorResponse;
import cat.community.nyangmunity.image.config.TenorConfig;
import cat.community.nyangmunity.image.entity.Image;
import cat.community.nyangmunity.image.repository.ImageRepository;
import cat.community.nyangmunity.image.response.UploadImageResponse;
import cat.community.nyangmunity.image.util.S3ImageUtil;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final S3ImageUtil s3ImageUtil;
    private final TenorConfig tenorConfig;

    /***
     * // 업로드 할 URL을 반환해주는 메서드
     * @param filename // 파일 이름을 전달 받으면 UUID 파일 이름으로 변환, 이를 DB에 저장
     * @return // 이후 이미지를 업로드 할 수 있는 presigned url, 변환된 이름의 filePath, 할당된 imageId를 반환한다.
     */
    public UploadImageResponse createImageInfo(String filename) {
        String filePath = generateRandomUUIDFilePath(filename);
        Image image = Image.builder()
                .name(filename)
                .url(filePath)
                .post(null)
                .build();

        Image savedImage = imageRepository.save(image);
        return UploadImageResponse.from(generateURL(filePath), filePath, savedImage.getId());
    }

    private String generateRandomUUIDFilePath(String filename) {
        return UUID.randomUUID() + getExtension(filename);
    }

    private URL generateURL(String filePath) {
        return s3ImageUtil.generatePresignedUrl(filePath);
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.'));
    }

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

    @Transactional(readOnly = true)
    public List<Image> getAllImages() {
        return imageRepository.findAll();
    }

    @Transactional
    public void saveImages(List<Image> saveMemeList) {
        imageRepository.saveAll(saveMemeList);
    }

    @Transactional
    public void deleteImages(List<Image> deleteMemeList) {
        imageRepository.deleteAll(deleteMemeList);
    }

    @Transactional(readOnly = true)
    public Page<ImageResponse> getImageList(int page) {
        Pageable pageable = PageRequest.of(page, 30);
        return convertToImageResponse(imageRepository.findAll(pageable));
    }

    private Page<ImageResponse> convertToImageResponse(Page<Image> memes) {
        return memes.map(meme -> ImageResponse.builder()
            .id(meme.getId())
            .url(meme.getUrl())
            .build());
    }
}
