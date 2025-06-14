package se499.kayaanbackend.security.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import se499.kayaanbackend.security.user.User;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

  @Value("${application.security.jwt.secret-key}")
  private String secretKey;
  @Value("${application.security.jwt.expiration}")
  private long jwtExpiration;
  @Value("${application.security.jwt.refresh-token.expiration}")
  private long refreshExpiration;

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public String generateToken(UserDetails userDetails) {
    Map<String, Object> extraClaims = new HashMap<>();

    // Add roles to JWT
    List<String> roles = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());
    extraClaims.put("roles", roles);

    // If userDetails is our User entity, use ID as subject
    if (userDetails instanceof User) {
      User user = (User) userDetails;
      extraClaims.put("username", user.getUsername());
      extraClaims.put("email", user.getEmail());
      return buildTokenWithUserId(extraClaims, user, jwtExpiration);
    }

    return generateToken(extraClaims, userDetails);
  }

  public String generateToken(
          Map<String, Object> extraClaims,
          UserDetails userDetails
  ) {
    // Ensure roles are included
    if (!extraClaims.containsKey("roles")) {
      List<String> roles = userDetails.getAuthorities().stream()
              .map(GrantedAuthority::getAuthority)
              .collect(Collectors.toList());
      extraClaims.put("roles", roles);
    }

    return buildToken(extraClaims, userDetails, jwtExpiration);
  }

  public String generateRefreshToken(UserDetails userDetails) {
    Map<String, Object> extraClaims = new HashMap<>();

    // Add minimal claims for refresh token
    if (userDetails instanceof User) {
      User user = (User) userDetails;
      extraClaims.put("username", user.getUsername());
      return buildTokenWithUserId(extraClaims, user, refreshExpiration);
    }

    return buildToken(extraClaims, userDetails, refreshExpiration);
  }

  private String buildToken(
          Map<String, Object> extraClaims,
          UserDetails userDetails,
          long expiration
  ) {
    return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
  }

  private String buildTokenWithUserId(
          Map<String, Object> extraClaims,
          User user,
          long expiration
  ) {
    return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(user.getId().toString()) // Use ID as subject
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String subject = extractUsername(token);

    // Check if subject is user ID or username
    if (userDetails instanceof User) {
      User user = (User) userDetails;
      // Support both ID and username as subject
      return (subject.equals(user.getId().toString()) || subject.equals(user.getUsername()))
              && !isTokenExpired(token);
    }

    return (subject.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(String token) {
    return Jwts
            .parser()
            .setSigningKey(getSignInKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
  }

  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  // Additional helper methods
  public List<String> extractRoles(String token) {
    return extractClaim(token, claims -> claims.get("roles", List.class));
  }

  public String extractEmail(String token) {
    return extractClaim(token, claims -> claims.get("email", String.class));
  }

  public String extractUsernameFromClaims(String token) {
    return extractClaim(token, claims -> claims.get("username", String.class));
  }
}