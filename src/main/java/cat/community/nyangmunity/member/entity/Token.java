package cat.community.nyangmunity.member.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@NoArgsConstructor
@RedisHash(value = "token", timeToLive = 60*60*24*7)
public class Token {

    @Id
    private String refreshToken;

    @Indexed
    private Long memberId;

    @Builder
    public Token(String refreshToken, Long memberId) {
        this.refreshToken = refreshToken;
        this.memberId = memberId;
    }
}
