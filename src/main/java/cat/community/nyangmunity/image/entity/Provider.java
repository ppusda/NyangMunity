package cat.community.nyangmunity.image.entity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Provider {
    NYANGMUNITY("Nyangmunity"),
    TENOR("Tenor");

    private final String provider;

    public static List<String> getProviderNames() {
        return Arrays.stream(Provider.values())
            .map(Provider::getProvider)
            .collect(Collectors.toList());
    }
}
