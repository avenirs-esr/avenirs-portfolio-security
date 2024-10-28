package fr.avenirsesr.portfolio.security.filter;


import fr.avenirsesr.portfolio.security.models.OIDCIntrospectResponse;
import fr.avenirsesr.portfolio.security.services.AuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * <h1>CASTokenAuthenticationFilter</h1>
 * <p>
 * Description: CASTokenAuthenticationFilter Spring Security filter for JWT.
 * </p>
 *
 * <h2>Version:</h2>
 * 1.0.0
 *
 * <h2>Author:</h2>
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 * 21/10/2024
 */
@Slf4j
@AllArgsConstructor
public class CASTokenAuthenticationFilter extends OncePerRequestFilter {

     private AuthenticationService authenticationService;




    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        log.trace("doFilterInternal, requested end point: {}", request.getRequestURI());
        String token = getTokenFromRequest(request);

        if (StringUtils.hasText(token)) {
            log.trace("doFilterInternal hasText(token) is true");

            OIDCIntrospectResponse introspectResponse = authenticationService.introspectAccessToken(token);
            log.trace("doFilterInternal introspectResponse: {}", introspectResponse);

            if (introspectResponse.isActive()) {
                String username = introspectResponse.getUniqueSecurityName();
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username, token, new ArrayList<>()));
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Retrieves the token from the request.
     *
     * @param request The request from which teh access token has to be retrieved.
     * @return The access token if found, null otherwise.
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        log.trace("getTokenFromRequest ");
        String bearerToken = request.getHeader("x-authorization");
        log.trace("getTokenFromRequest bearerToken: {}", bearerToken);
       return bearerToken;
    }
}

