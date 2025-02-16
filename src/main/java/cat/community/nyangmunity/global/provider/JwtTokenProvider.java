package cat.community.nyangmunity.global.provider;

import cat.community.nyangmunity.global.config.AppConfig;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.util.Collections;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
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
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public boolean validateToken(String token) {
        try{
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch(JwtException e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        User principal = new User(claims.getSubject(), "",  Collections.emptyList());
        return new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
    }
}
