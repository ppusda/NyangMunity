package cat.community.NyangMunity.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity @Table(name = "BoardImage")
@Getter @Setter
public class BoardImage {

    @Id @Column(name = "IMAGE_ID")
    private Long Id;

    @ManyToOne
    @JoinColumn(name = "BOARD_ID")
    private Board board;

    @Column(name = "PATH")
    private String path;

    public void setBoard(Board board) { // 연관관계 편의 메서드
        this.board = board;
        if(!board.getBoardImages().contains(this)){
            board.getBoardImages().add(this);
        }
    }
}
