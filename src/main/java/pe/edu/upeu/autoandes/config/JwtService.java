package pe.edu.upeu.autoandes.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key;
    private final long expiration = 3600;
    public JwtService(@Value("${app.security.jwt-secret:AutoAndes-secret-key-change-in-production-2026}") String secret) {
        key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    public String generate(String username) {
        Instant now = Instant.now();
        return Jwts.builder().subject(username).issuedAt(Date.from(now)).expiration(Date.from(now.plusSeconds(expiration)))
                .signWith(key).compact();
    }
    public String username(String token) { return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject(); }
    public long expiration() { return expiration; }
}
