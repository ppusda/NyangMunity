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

    private final ImageService imageService;

    @Override
    public List<Image> process(List<TenorResponse> tenorResponses) {
        List<Image> existImages = imageService.getAllImages();
        Set<String> existImageIds = existImages.stream()
                .map(Image::getId)
                .collect(Collectors.toSet());

        List<Image> newImages = tenorResponses.stream()
                .map(this::convertTenorResponseToImages)
                .toList();

        List<Image> imagesToAdd = filteringForAddingImages(newImages, existImageIds);

        Set<String> newIds = newImages.stream()
                .map(Image::getId)
                .collect(Collectors.toSet());

        List<Image> imagesToDelete = filteringForDeleteImages(existImages, newIds);
        imageService.deleteImages(imagesToDelete);

        return imagesToAdd;
    }

    private Image convertTenorResponseToImages(TenorResponse tenorResponse) {
        return Image.builder()
                .id(tenorResponse.id())
                .url(tenorResponse.media_formats().gif().url())
                .provider(Provider.TENOR)
                .build();
    }

    private List<Image> filteringForAddingImages(List<Image> images, Set<String> existIdSet) {
        return images.stream()
                .filter(image -> !existIdSet.contains(image.getId()))
                .toList();
    }

    private List<Image> filteringForDeleteImages(List<Image> images, Set<String> newIdSet) {
        return images.stream()
                .filter(image -> !newIdSet.contains(image.getId()))
                .toList();
    }
}
