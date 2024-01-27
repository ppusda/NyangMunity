package cat.community.NyangMunity.board.entity;

import cat.community.NyangMunity.board.editor.BoardEditor;
import cat.community.NyangMunity.board.editor.BoardEditor.BoardEditorBuilder;
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
    public Board(String title, String content, User user, ArrayList<BoardImage> boardImages, LocalDateTime createDate) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.boardImages = boardImages;
        this.createDate = createDate;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column(columnDefinition = "text")
    private String content;

    @Column
    private LocalDateTime createDate;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BoardImage> boardImages = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BoardLike> boardLikes = new ArrayList<>();

    public BoardEditorBuilder toEditor() {
        return BoardEditor.builder()
                .title(title)
                .content(content);
    }

    public void edit(BoardEditor boardEditor){
        title = boardEditor.getTitle();
        content = boardEditor.getContent();
    }

    public void setUser(User user) { // 연관관계 편의 메서드
        this.user = user;
        if(!user.getBoards().contains(this)){
            user.getBoards().add(this);
        }
    }

    public void setBoardImages(BoardImage boardImage) { // 연관관계 편의 메서드
        boardImages.add(boardImage);
        boardImage.setBoard(this);
    }
}
