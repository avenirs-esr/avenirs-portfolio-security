package fr.avenirsesr.portfolio.security.model;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <h1>AccessControlRevokeRequest</h1>
 * <p>
 * Description: used to revoke a granted access.
 *
 * </p>
 *
 * <h2>Version:</h2>
 * 1.0.0
 *
 * <h2>Author:</h2>
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 * 17/10/2024
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class AccessControlRevokeRequest {

    /** The login of the principal. */
    private String login;

    /** The id of the role to grant. */
    private Long roleId;

    /** The id of the scope */
    private Long scopeId;

    /** The id of the context. */
    private Long contextId;

}
