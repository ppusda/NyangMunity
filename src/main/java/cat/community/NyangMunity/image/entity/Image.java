package cat.community.NyangMunity.image.entity;

import cat.community.NyangMunity.board.entity.Board;
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
@Getter @Table(name="image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String url;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @Builder
    public Image(String name, String path, Board board) {
        this.name = name;
        this.url = url;
        this.board = board;
    }

    public void addBoard(Board board) {
        this.board = board;
    }
}

