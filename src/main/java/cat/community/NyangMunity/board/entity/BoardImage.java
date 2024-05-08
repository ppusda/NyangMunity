package cat.community.NyangMunity.board.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter @Table(name="board_image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardImage {

    @Builder
    public BoardImage(String name, Long size, Board board, String path) {
        this.name = name;
        this.size = size;
        this.board = board;
        this.path = path;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private Long size;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @Column
    private String path;

    public void setBoard(Board board) {
        this.board = board;
    }
}
