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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private LocalDateTime createDate;

    @Column
    private LocalDate birthday;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Board> boards = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Session> sessions = new ArrayList<>();

    public Session addSession() {
        Session session = Session.builder()
                .user(this)
                .build();
        sessions.add(session);
        return session;
    }
}
