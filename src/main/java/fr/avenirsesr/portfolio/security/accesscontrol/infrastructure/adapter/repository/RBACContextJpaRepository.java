package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACContextEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 *
 * <h1>RBACContextRepository</h1>
 *
 * <p>Description: Repository for the RBACContext used as application context (restriction on
 * assignments).
 *
 * <h2>Version:</h2>
 *
 * 1.0
 *
 * <h2>Author:</h2>
 *
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 *
 * 1 Oct 2024
 */
public interface RBACContextJpaRepository
    extends JpaRepository<RBACContextEntity, UUID>, JpaSpecificationExecutor<RBACContextEntity> {}
