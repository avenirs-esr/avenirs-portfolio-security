package fr.avenirsesr.portfolio.security.repository;

import fr.avenirsesr.portfolio.security.model.RBACAssignment;

/**
 * <h1>RBACAssignmentRepositoryCustom</h1>
 * <p>
 * <b>Description:</b> Used to overload save method to handle entities collections in scope and context.
 * </p>
 *
 * <h2>Version:</h2>
 * 1.0.0
 *
 * <h2>Author:</h2>
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 * 05/12/2024
 */

public interface RBACAssignmentRepositoryCustom {
    RBACAssignment saveWithRelations(RBACAssignment assignment);
}

