package fr.avenirsesr.portfolio.security.model;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.UUID;

/**
 * <h1>AccessControlGrantRequest</h1>
 * <p>
 * Description: AccessControlGrantRequest is used to grant a role to a principal.
 * The granted role can be associated to specific resources and limited to an application context.
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
public class AccessControlGrantRequest {

    /** The login of the principal. */
    private String login;

    /** The id of the role to grant. */
    private UUID roleId;

    /** The ids of the resources (optional). */
    private UUID[] resourceIds;

    /** Application context: the start date of the validity period (optional). */
    private String validityStart;

    /** Application context: the end date of the validity period (optional). */
    private String validityEnd;

    /**  Application context: the ids of the structures (optional).*/
    private UUID[] structureIds;

}
