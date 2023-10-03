package cat.community.NyangMunity.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Table(name = "USER")
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

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private LocalDateTime createDate;

    @Column
    private LocalDate birthday;

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Board> boards = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Token> tokens = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BoardLike> boardLikes = new ArrayList<>();

    public UserEditor.UserEditorBuilder toEditor() {
        return UserEditor.builder()
                .nickname(nickname)
                .password(password)
                .birthday(birthday);
    }

    public void edit(UserEditor userEditor) {
        nickname = userEditor.getNickname();
        password = userEditor.getPassword();
        birthday = userEditor.getBirthday();
    }

    public Token addToken(String refreshToken) {
        Token token = Token.builder()
                .user(this)
                .refreshToken(refreshToken)
                .build();
        tokens.add(token);
        return token;
    }
}
