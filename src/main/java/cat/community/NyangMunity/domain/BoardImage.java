package cat.community.NyangMunity.domain;

import lombok.*;

import javax.persistence.*;

@Entity @Table(name = "BoardImage")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardImage {

    @Builder
    public BoardImage(String name, Long size, Board board, String path) {
        this.name = name;
        this.size = size;
        this.board = board;
        this.path = path;
    }

    @Id @Column(name = "IMAGE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "SIZE")
    private Long size;

    @ManyToOne
    @JoinColumn(name = "BOARD_ID")
    private Board board;

    @Column(name = "PATH")
    private String path;

}
