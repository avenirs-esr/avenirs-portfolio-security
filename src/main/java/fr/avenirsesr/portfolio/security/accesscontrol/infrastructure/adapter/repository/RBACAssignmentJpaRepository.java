package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACAssignmentEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 *
 * <h1>RBACAssignmentRepository</h1>
 *
 * <p><b>Description:</b> Repository for RBACAssignment.
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
 * 14/10/2024
 */
public interface RBACAssignmentJpaRepository
    extends JpaRepository<RBACAssignmentEntity, UUID>,
        JpaSpecificationExecutor<RBACAssignmentEntity>,
        RBACAssignmentJpaRepositoryCustom {}
