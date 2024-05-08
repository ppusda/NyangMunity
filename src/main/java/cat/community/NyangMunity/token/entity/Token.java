package cat.community.NyangMunity.token.entity;

import cat.community.NyangMunity.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String refreshToken;


    @ManyToOne
    @JoinColumn(unique = true)
    private User user;

    @Builder
    public Token(String refreshToken, User user) {
        this.refreshToken = refreshToken;
        this.user = user;
    }
}
