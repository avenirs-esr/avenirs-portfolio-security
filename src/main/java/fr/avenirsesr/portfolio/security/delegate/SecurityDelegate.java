package fr.avenirsesr.portfolio.security.delegate;


import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * <h1>SecurityDelegate</h1>
 * <p>
 * Description: SecurityDelegate is used to check Authentication status and retrieve user login.
 * </p>
 *
 * <h2>Version:</h2>
 * 1.0.0
 *
 * <h2>Author:</h2>
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 * 28/10/2024
 */
@Component
public class SecurityDelegate {

    /**
     * Gives the user login fetched from the authentication context.
     * @return The user login.
     */
    public String getAuthenticatedUserLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return authentication.getName();
    }
}
