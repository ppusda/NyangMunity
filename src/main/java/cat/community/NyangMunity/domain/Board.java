package cat.community.NyangMunity.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Entity @Table(name = "BOARD")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Board {

    @Builder
    public Board(String title, String content) {
        this.title = title;
        this.content = content;
    }

    @Id @Column(name = "BOARD_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "CREATE_DATE")
    private String createDate;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private User user;

    @Column(name = "NICKNAME")
    private String nickname;

}
