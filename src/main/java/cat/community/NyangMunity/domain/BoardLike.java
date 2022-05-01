package cat.community.NyangMunity.domain;

import javax.persistence.*;

@Entity @Table(name = "BOARD_LIKE")
public class BoardLike {

    @Id @Column(name = "LIKE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "BOARD_ID")
    private Board board;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column(name = "LIKE_CHECK")
    private Integer likeCheck;
}
