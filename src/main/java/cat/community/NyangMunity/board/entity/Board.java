package cat.community.NyangMunity.board.entity;

import cat.community.NyangMunity.board.editor.BoardEditor;
import cat.community.NyangMunity.board.editor.BoardEditor.BoardEditorBuilder;
import cat.community.NyangMunity.image.entity.Image;
import cat.community.NyangMunity.user.entity.User;
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
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity @Table(name = "board")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Board {

    @Builder
    public Board(String content, User user, ArrayList<Image> images, LocalDateTime createDate) {
        this.content = content;
        this.user = user;
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
    @JoinColumn(name="user_id")
    private User user;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BoardLike> likes = new ArrayList<>();

    public BoardEditorBuilder toEditor() {
        return BoardEditor.builder()
                .content(content);
    }

    public void edit(BoardEditor boardEditor){
        content = boardEditor.getContent();
    }

    public void setUser(User user) {
        this.user = user;
        if(!user.getBoards().contains(this)){
            user.getBoards().add(this);
        }
    }

    public void setBoardImages(Image image) {
        images.add(image);
        image.addBoard(this);
    }
}
