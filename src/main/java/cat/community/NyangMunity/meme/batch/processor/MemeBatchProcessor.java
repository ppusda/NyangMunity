package cat.community.NyangMunity.meme.batch.processor;

import cat.community.NyangMunity.meme.entity.Meme;
import cat.community.NyangMunity.meme.entity.Provider;
import cat.community.NyangMunity.meme.repository.MemeRepository;
import cat.community.NyangMunity.meme.response.TenorResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemeBatchProcessor implements ItemProcessor<List<TenorResponse>, List<Meme>> {

    @Override
    public List<Meme> process(List<TenorResponse> tenorResponses) {
        List<Meme> memes = new ArrayList<>();
        for (TenorResponse tenorResponse : tenorResponses) {
            Meme meme = Meme.builder()
                    .id(tenorResponse.id())
                    .url(tenorResponse.media_formats().gif().url())
                    .provider(Provider.TENOR.getProvider())
                    .build();

            memes.add(meme);
        }

        return memes;
    }
}
