package se499.kayaanbackend.security.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import se499.kayaanbackend.security.token.TokenRepository;

import se499.kayaanbackend.security.user.UserDao;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
  private final TokenRepository tokenRepository;
  private final UserDao userDao;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    if (request.getServletPath().contains("/api/v1/auth")) {
      filterChain.doFilter(request, response);
      return;
    }
    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String userIdentifier;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }
    jwt = authHeader.substring(7);
    userIdentifier = jwtService.extractUsername(jwt);
    if (userIdentifier != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails;
      if (userIdentifier.matches("\\d+")) {          // subject is numeric -> treat as id
        Long id = Long.parseLong(userIdentifier);
        var userOpt = userDao.findById(id);
        if (userOpt.isEmpty()) {
          filterChain.doFilter(request, response);
          return;
        }
        userDetails = userOpt.get();
      } else {                                       // normal case -> username
        userDetails = this.userDetailsService.loadUserByUsername(userIdentifier);
      }
      boolean isTokenValid = tokenRepository.findByToken(jwt)
              .map(t -> !t.isExpired() && !t.isRevoked())
              .orElse(false);
      if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
        // ---- build authorities from JWT roles claim (if present) ----
        List<String> rolesFromToken = jwtService.extractClaim(jwt, c -> c.get("roles", List.class));
        Collection<? extends GrantedAuthority> authorities =
                (rolesFromToken == null || rolesFromToken.isEmpty())
                        ? userDetails.getAuthorities()
                        : rolesFromToken.stream()
                                        .map(SimpleGrantedAuthority::new)
                                        .toList();
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            authorities
        );
        authToken.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }
    filterChain.doFilter(request, response);
  }
}
