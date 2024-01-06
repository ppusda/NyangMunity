package cat.community.NyangMunity.board.entity;

import cat.community.NyangMunity.board.editor.BoardEditor;
import cat.community.NyangMunity.board.editor.BoardEditor.BoardEditorBuilder;
import cat.community.NyangMunity.user.entity.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity @Table(name = "BOARD")
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
    @JsonBackReference
    @JoinColumn(name="user_id")
    private User user;

    @JsonManagedReference
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BoardImage> boardImages = new ArrayList<>();

    @JsonManagedReference
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
