package fr.avenirsesr.portfolio.security.repositories;

import fr.avenirsesr.portfolio.security.models.RBACAssignmentPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import fr.avenirsesr.portfolio.security.models.RBACAssignment;

/**
 * <h1>RBACAssignmentRepository</h1>
 * <p>
 * <b>Description:</b> Repository for RBACAssignment.
 * </p>
 *
 * <h2>Version:</h2>
 * 1.0.0
 *
 * <h2>Author:</h2>
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 * 14/10/2024
 */

public interface RBACAssignmentRepository extends JpaRepository<RBACAssignment, RBACAssignmentPK>, JpaSpecificationExecutor<RBACAssignment>  {

}