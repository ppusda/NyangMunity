package cat.community.nyangmunity.post.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import cat.community.nyangmunity.member.entity.Member;
import cat.community.nyangmunity.post.editor.PostEditor;
import cat.community.nyangmunity.post.editor.PostEditor.BoardEditorBuilder;
import cat.community.nyangmunity.image.entity.Image;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Entity
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Post {

    @Builder
    public Post(String content, Member member, ArrayList<Image> images, LocalDateTime createDate) {
        this.content = content;
        this.member = member;
        this.images = images;
        this.createDate = createDate;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "text")
    private String content;

    @Column
    private LocalDateTime createDate;

    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostLike> likes = new ArrayList<>();

    public BoardEditorBuilder toEditor() {
        return PostEditor.builder()
                .content(content);
    }

    public void edit(PostEditor postEditor){
        content = postEditor.getContent();
    }

    public void setMember(Member member) {
        this.member = member;
        if(!member.getPosts().contains(this)){
            member.getPosts().add(this);
        }
    }
}
