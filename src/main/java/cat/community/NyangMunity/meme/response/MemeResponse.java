package cat.community.NyangMunity.meme.response;

import lombok.Builder;

@Builder
public record MemeResponse (
        String id,
        String url
) {

}
