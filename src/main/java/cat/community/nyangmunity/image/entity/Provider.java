package cat.community.nyangmunity.image.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Provider {
    NYANGMUNITY("NyangMunity"),
    TENOR("Tenor");


    private final String provider;
}
