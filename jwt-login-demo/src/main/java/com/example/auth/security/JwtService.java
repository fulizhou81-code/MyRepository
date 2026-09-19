package com.example.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key;
    private final Duration expiration;
    public JwtService(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.expiration}") Duration expiration) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expiration = expiration;
    }
    public String generate(UserDetails user) {
        Instant now = Instant.now();
        return Jwts.builder().subject(user.getUsername()).issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration))).signWith(key).compact();
    }
    public String username(String token) { return claims(token).getSubject(); }
    public boolean valid(String token, UserDetails user) {
        try { Claims c = claims(token); return c.getSubject().equals(user.getUsername()) && c.getExpiration().after(new Date()); }
        catch (RuntimeException e) { return false; }
    }
    public long expirationSeconds() { return expiration.toSeconds(); }
    private Claims claims(String token) { return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload(); }
}
