package cat.community.nyangmunity.image.entity;

import cat.community.nyangmunity.post.entity.Post;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {

    @Id
    private String id;

    @Column
    private String name;

    @Column
    private String url;

    @Column
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public Image(String id, String name, String url, Post post, Provider provider) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.post = post;
        this.provider = provider;
    }

    public void addPost(Post post) {
        this.post = post;
    }
}

