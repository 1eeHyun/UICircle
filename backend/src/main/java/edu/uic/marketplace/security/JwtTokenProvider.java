package edu.uic.marketplace.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKeyBase64;

    @Value("${jwt.expiration-ms:900000}")
    private long jwtExpirationInMs;

    @Value("${jwt.issuer:uic-marketplace}")
    private String issuer;

    @Value("${jwt.audience:frontend}")
    private String audience;

    private SecretKey secretKey;
    private static final long CLOCK_SKEW_SEC = 60;

    @PostConstruct
    public void init() {

        byte[] decodedKey = Base64.getDecoder().decode(secretKeyBase64);
        if (decodedKey.length < 64) {
            throw new IllegalStateException("jwt.secret must be >= 64 bytes after Base64 decoding for HS512");
        }
        this.secretKey = Keys.hmacShaKeyFor(decodedKey);
    }

    public String generateToken(String username) {

        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtExpirationInMs);
        return Jwts.builder()
                .setSubject(username)
                .setIssuer(issuer)
                .setAudience(audience)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public long getJwtExpirationInMs() {
        return jwtExpirationInMs;
    }

    public String getUsernameFromJWT(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .setAllowedClockSkewSeconds(CLOCK_SKEW_SEC)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {

        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .setAllowedClockSkewSeconds(CLOCK_SKEW_SEC)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}
