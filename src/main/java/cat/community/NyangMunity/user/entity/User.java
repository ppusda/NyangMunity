package cat.community.NyangMunity.user.entity;

import cat.community.NyangMunity.board.entity.Board;
import cat.community.NyangMunity.board.entity.BoardLike;
import cat.community.NyangMunity.user.editor.UserEditor;
import cat.community.NyangMunity.user.editor.UserEditor.UserEditorBuilder;
import cat.community.NyangMunity.token.entity.Token;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Table(name = "user")
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Board> boards = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BoardLike> boardLikes = new ArrayList<>();

    public UserEditorBuilder toEditor() {
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
}
