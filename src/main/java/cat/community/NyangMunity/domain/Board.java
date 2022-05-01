package cat.community.NyangMunity.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity @Table(name = "BOARD")
public class Board {

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
