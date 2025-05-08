package cat.community.nyangmunity.image.batch.writer;

import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import cat.community.nyangmunity.image.entity.Image;
import cat.community.nyangmunity.image.service.ImageCommandService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ImageBatchWriter implements ItemWriter<List<Image>> {

	private final ImageCommandService imageCommandService;

	@Override
	public void write(Chunk<? extends List<Image>> imageChunk) {
		for (List<Image> images : imageChunk) {
			imageCommandService.saveImages(images);
		}
	}
}
