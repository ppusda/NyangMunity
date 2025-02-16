package cat.community.nyangmunity.image.batch.processor;

import cat.community.nyangmunity.image.entity.Image;
import cat.community.nyangmunity.image.entity.Provider;
import cat.community.nyangmunity.image.batch.response.TenorResponse;
import cat.community.nyangmunity.image.service.ImageService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageBatchProcessor implements ItemProcessor<List<TenorResponse>, List<Image>> {

    private final ImageService memeService;

    @Override
    public List<Image> process(List<TenorResponse> tenorResponses) {
        List<Image> existImages = memeService.getAllImages();
        Set<String> existImageIds = existImages.stream()
                .map(Image::getId)
                .collect(Collectors.toSet());

        List<Image> newImages = tenorResponses.stream()
                .map(this::convertTenorResponseToImages)
                .toList();

        // 새로 추가할 밈 목록
        List<Image> memesToAdd = filteringForAddingImages(newImages, existImageIds);

        Set<String> newIds = newImages.stream()
                .map(Image::getId)
                .collect(Collectors.toSet());

        // 제거할 밈 목록
        List<Image> memesToDelete = filteringForDeleteImages(existImages, newIds);
        memeService.deleteImages(memesToDelete);

        return memesToAdd;
    }

    private Image convertTenorResponseToImages(TenorResponse tenorResponse) {
        return Image.builder()
                .id(tenorResponse.id())
                .url(tenorResponse.media_formats().gif().url())
                .provider(Provider.TENOR)
                .build();
    }

    private List<Image> filteringForAddingImages(List<Image> memes, Set<String> existIdSet) {
        return memes.stream()
                .filter(meme -> !existIdSet.contains(meme.getId()))
                .toList();
    }

    private List<Image> filteringForDeleteImages(List<Image> memes, Set<String> newIdSet) {
        return memes.stream()
                .filter(meme -> !newIdSet.contains(meme.getId()))
                .toList();
    }
}
