package cat.community.NyangMunity.meme.batch.writer;

import cat.community.NyangMunity.meme.entity.Meme;
import cat.community.NyangMunity.meme.service.MemeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemeBatchWriter implements ItemWriter<List<Meme>> {

    private final MemeService memeService;

    @Override
    public void write(Chunk<? extends List<Meme>> memeChunk) {
        for (List<Meme> memes : memeChunk) {
            memeService.saveMemes(memes);
        }
    }
}
