package com.grownet.security;

import com.grownet.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key;
    private final long expirationMs;

    public JwtService(@Value("${app.jwt.secret:}") String secret, @Value("${app.jwt.expiration-ms:86400000}") long expirationMs) {
        if (secret == null || secret.isBlank()) throw new IllegalStateException("JWT_SECRET must be configured as a Base64-encoded secret of at least 32 bytes.");
        byte[] keyBytes;
        try { keyBytes = Decoders.BASE64.decode(secret); }
        catch (RuntimeException ex) { throw new IllegalStateException("JWT_SECRET must be Base64 encoded."); }
        if (keyBytes.length < 32) throw new IllegalStateException("JWT_SECRET must decode to at least 32 bytes.");
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
    }

    public String generateToken(User user) {
        Date now = new Date();
        return Jwts.builder().subject(user.getEmail()).claim("role", user.getRole().name())
                .issuedAt(now).expiration(new Date(now.getTime() + expirationMs)).signWith(key).compact();
    }
    public String extractUsername(String token) { return parse(token).getSubject(); }
    public boolean isTokenValid(String token, UserDetails details) {
        Claims claims = parse(token);
        return claims.getSubject().equalsIgnoreCase(details.getUsername()) && claims.getExpiration().after(new Date());
    }
    private Claims parse(String token) { return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload(); }
}
