package cat.community.NyangMunity.config;

import cat.community.NyangMunity.exception.Unauthorized;
import cat.community.NyangMunity.request.UserSession;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private SecretKey key;

    public JwtTokenProvider(AppConfig appConfig) {
        this.key = Keys.hmacShaKeyFor(appConfig.getJwtKey());
    }

    public String createAccessToken(Long userId) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .signWith(key)
                .setExpiration(new Date(now.getTime() + (1000L * 60 * 30)))
                .setIssuedAt(now)
                .compact();
    }

    public String createRefreshToken(Long userId) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .signWith(key)
                .setExpiration(new Date(now.getTime() + (1000L * 60 * 60 * 3)))
                .setIssuedAt(now)
                .compact();
    }

    public Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody(); // This line will not be reached if the token is expired.
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public boolean validateToken(String token) {
        try{
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token); // validate가 정상적으로 이루어졌다면 다음단계로 넘어가지만, 그렇지않으면 여기서 멈춤.
            return true;
        } catch(JwtException e) {
            return false;
        }
    }
}
