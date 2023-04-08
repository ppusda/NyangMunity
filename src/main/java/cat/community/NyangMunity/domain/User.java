package cat.community.NyangMunity.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "USER")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class User {

    @Builder
    public User(String email, String password, String nickname, LocalDate birthday, LocalDateTime createDate) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.birthday = birthday;
        this.createDate = createDate;
    }

    @Id @Column(name = "USER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "NICKNAME", nullable = false)
    private String nickname;

    @Column(name = "CREATE_DATE", nullable = false)
    private LocalDateTime createDate;

    @Column(name = "BIRTHDAY")
    private LocalDate birthday;

    @OneToMany(mappedBy = "user")
    private List<Board> boards = new ArrayList<>();


}
