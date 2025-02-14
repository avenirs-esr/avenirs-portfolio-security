package fr.avenirsesr.portfolio.security.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import fr.avenirsesr.portfolio.security.model.RBACActionRoute;

import java.util.UUID;

/**
 * Repository for RBACActionRoute.
 */
public interface RBACActionRouteRepository extends JpaRepository<RBACActionRoute, UUID>, JpaSpecificationExecutor<RBACActionRoute>   {
}
