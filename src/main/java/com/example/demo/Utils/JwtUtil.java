package com.example.demo.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private long expiration;

    public SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserDetails userDetails){
        List<String>roles=userDetails.getAuthorities()
                .stream()
                .map((GrantedAuthority grantedAuthority)-> grantedAuthority.getAuthority())
                .collect(Collectors.toList());
        return Jwts.builder()
                .subject(userDetails.getUsername())//identity of user like email,id,username
                .claim("roles",roles)//additional info like roles
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() +expiration))
                .signWith(getSigningKey())
                .compact();// to convert into string
    }

    public String extractEmail(String token){
        return extractAllClaims(token).getSubject();
    }
    //not required ; code can work ; just to hide warning
    @SuppressWarnings("unchecked")//to compile warning
    public List<String> extractRoles(String token){
        // below returns Object
        return (List<String>) extractAllClaims(token).get("roles");
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        try{
            String email=extractEmail(token);
            return email.equals(userDetails.getUsername()) &&
                                !isTokenExpired(token);
        }catch (Exception e){
            return false;
        }
    }
    public boolean isTokenExpired(String token){
        return extractAllClaims(token).getExpiration().before(new Date());
    }

}
