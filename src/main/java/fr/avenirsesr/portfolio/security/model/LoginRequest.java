package fr.avenirsesr.portfolio.security.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * <h1>LoginRequest</h1>
 * <p>
 * Description: LoginRequest used to retrieve an Access token.
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
@Data
@Accessors(chain = true)
public class LoginRequest {

    private String login;

    @ToString.Exclude
    private String password;

    @ToString.Include(name = "password")
    @Schema(hidden = true)
    public String getMaskedPassword() {
        return password != null ? "****"  : null;
    }
}
