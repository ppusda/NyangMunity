package cat.community.NyangMunity.domain;

import cat.community.NyangMunity.request.BoardEdit;
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

    @Id @Column(name = "BOARD_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "CREATE_DATE")
    private LocalDateTime createDate;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @OneToMany(mappedBy = "board")
    private List<BoardImage> boardImages = new ArrayList<>();

    public BoardEditor.BoardEditorBuilder toEditor() {
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

    public void setBoardImages(List<BoardImage> boardImages) { // 연관관계 편의 메서드
        this.boardImages = boardImages;
        for(BoardImage boardImage: this.getBoardImages()){
            if(boardImages.contains(boardImage)){
                boardImages.add(boardImage);
            }
        }
    }
}
