package com.alkemy.java2.clase15.authsecurity.service;

import io.jsonwebtoken.SignatureAlgorithm;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {

  @Value("${app.jwt.secret}")
  private String secretKey;

  @Value("${app.jwt.expiration}")
  private Long expirationMs;

  private Key signingKey;

  public String generateToken(UserDetails user) {
    return Jwts.builder()
        .subject(user.getUsername())
        .claim("roles", user.getAuthorities().stream()
            .map(auth -> auth.getAuthority())
            .toList())
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expirationMs))
        .signWith(getSigningKey(), resolveAlgorithm())
        .compact();
  }

  public boolean isTokenValid(String token, UserDetails user) {
    try {
      return user.getUsername().equals(extractClaim(token, Claims::getSubject)) &&
          !extractClaim(token, Claims::getExpiration).before(new Date());
    } catch (JwtException e) {
      return false;
    }
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> resolver) {
    try {
      return resolver.apply(Jwts.parser()
          .setSigningKey(getSigningKey())
          .build()
          .parseClaimsJws(token)
          .getBody());
    } catch (ExpiredJwtException e) {
      return resolver.apply(e.getClaims());
    }
  }

  private Key getSigningKey() {
    if (signingKey == null) {
      signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }
    return signingKey;
  }

  private SignatureAlgorithm resolveAlgorithm() {
    return SignatureAlgorithm.HS256; // Usa directamente HS256, que es común y compatible
  }
}
