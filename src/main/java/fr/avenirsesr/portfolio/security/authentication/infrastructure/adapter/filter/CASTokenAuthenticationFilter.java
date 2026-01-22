package fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.filter;

import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCIntrospection;
import fr.avenirsesr.portfolio.security.authentication.domain.port.input.AuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 *
 *
 * <h1>CASTokenAuthenticationFilter</h1>
 *
 * <p>Description: CASTokenAuthenticationFilter Spring Security filter for JWT.
 *
 * <h2>Version:</h2>
 *
 * 1.0.0
 *
 * <h2>Author:</h2>
 *
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 *
 * 21/10/2024
 */
@Slf4j
@AllArgsConstructor
public class CASTokenAuthenticationFilter extends OncePerRequestFilter {

  private AuthenticationService authenticationService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    log.trace("doFilterInternal, requested end point: {}", request.getRequestURI());
    String token = getTokenFromRequest(request);
    OIDCIntrospection introspection;
    if (StringUtils.hasText(token)) {
      log.trace("doFilterInternal hasText(token) is true");

      introspection = authenticationService.introspectAccessToken(token);
      log.trace("doFilterInternal introspection: {}", introspection);

      if (introspection != null && introspection.active()) {
        log.trace(
            "doFilterInternal introspectResponse is active, updating SecurityContext with new"
                + " authentication");
        String username = introspection.uniqueSecurityName();
        SecurityContextHolder.getContext()
            .setAuthentication(
                new UsernamePasswordAuthenticationToken(username, token, new ArrayList<>()));
      } else {
        log.trace("doFilterInternal introspectResponse is not active");
      }
    }

    filterChain.doFilter(request, response);
  }

  /**
   * Retrieves the token from the request.
   *
   * @param request The request from which the access token has to be retrieved.
   * @return The access token if found, null otherwise.
   */
  private String getTokenFromRequest(HttpServletRequest request) {
    log.trace("getTokenFromRequest ");
    String bearerToken = null;

    String authorization = request.getHeader("Authorization");
    if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
      bearerToken = authorization.substring("Bearer ".length()).trim();
    }

    if (!StringUtils.hasText(bearerToken)) {
      bearerToken = request.getHeader("x-authorization");
    }
    log.trace(
        "getTokenFromRequest bearerToken: {}",
        StringUtils.hasLength(bearerToken) ? "****" : bearerToken);
    return bearerToken;
  }
}
