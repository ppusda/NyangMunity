package cat.community.NyangMunity.meme.batch.processor;

import cat.community.NyangMunity.meme.entity.Meme;
import cat.community.NyangMunity.meme.entity.Provider;
import cat.community.NyangMunity.meme.repository.MemeRepository;
import cat.community.NyangMunity.meme.response.TenorResponse;
import cat.community.NyangMunity.meme.service.MemeService;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemeBatchProcessor implements ItemProcessor<List<TenorResponse>, List<Meme>> {

    private final MemeService memeService;

    @Override
    public List<Meme> process(List<TenorResponse> tenorResponses) {
        List<Meme> existMemes = memeService.getAllMemes();
        Set<String> existMemeIds = existMemes.stream()
                .map(Meme::getId)
                .collect(Collectors.toSet());

        List<Meme> newMemes = tenorResponses.stream()
                .map(this::convertTenorResponseToMemes)
                .toList();

        // 새로 추가할 밈 목록
        List<Meme> memesToAdd = filteringForAddingMemes(newMemes, existMemeIds);

        Set<String> newIds = newMemes.stream()
                .map(Meme::getId)
                .collect(Collectors.toSet());

        // 제거할 밈 목록
        List<Meme> memesToDelete = filteringForDeleteMemes(existMemes, newIds);
        memeService.deleteMemes(memesToDelete);

        return memesToAdd;
    }

    private Meme convertTenorResponseToMemes(TenorResponse tenorResponse) {
        return Meme.builder()
                .id(tenorResponse.id())
                .url(tenorResponse.media_formats().gif().url())
                .provider(Provider.TENOR.getProvider())
                .build();
    }

    private List<Meme> filteringForAddingMemes(List<Meme> memes, Set<String> existIdSet) {
        return memes.stream()
                .filter(meme -> !existIdSet.contains(meme.getId()))
                .toList();
    }

    private List<Meme> filteringForDeleteMemes(List<Meme> memes, Set<String> newIdSet) {
        return memes.stream()
                .filter(meme -> !newIdSet.contains(meme.getId()))
                .toList();
    }
}
