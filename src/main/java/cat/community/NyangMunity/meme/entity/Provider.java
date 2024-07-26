package cat.community.NyangMunity.meme.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Provider {
    TENOR("Tenor");

    private final String provider;
}
