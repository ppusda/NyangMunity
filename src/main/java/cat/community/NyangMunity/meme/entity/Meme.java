package cat.community.NyangMunity.meme.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Meme {

    @Id
    String id;

    @Column
    String url;

    @Column
    String provider;

    @Builder
    public Meme(String id, String url, String provider) {
        this.id = id;
        this.url = url;
        this.provider = provider;
    }
}
