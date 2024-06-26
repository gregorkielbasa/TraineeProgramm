package org.lager.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtTokenProvider {

    private final static int VALID_DURATION = 900_000; // 15 min
    private final static Key KEY = Jwts.SIG.HS256.key().build();

    private final UserDetailsManager userDetailsManager;

    public JwtTokenProvider(UserDetailsManager userDetailsManager) {
        this.userDetailsManager = userDetailsManager;
    }

    public Optional<UserDetails> getUser(String token) {
        if (token == null || token.isBlank() || isTokenExpired(token))
            return Optional.empty();
        String username = getUsernameFromToken(token);
        if (!userDetailsManager.userExists(username))
            return Optional.empty();
        return Optional.of(userDetailsManager.loadUserByUsername(username));
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + VALID_DURATION))
                .signWith(KEY)
                .compact();
    }

    private String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    private boolean isTokenExpired(String token) {
        Date expiration = getClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parser().verifyWith((SecretKey) KEY).build().parseSignedClaims(token).getPayload();
    }
}
