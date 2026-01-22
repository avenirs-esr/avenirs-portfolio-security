package fr.avenirsesr.portfolio.security.repository;

import fr.avenirsesr.portfolio.security.model.RBACActionRoute;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/** Repository for RBACActionRoute. */
public interface RBACActionRouteRepository
    extends JpaRepository<RBACActionRoute, UUID>, JpaSpecificationExecutor<RBACActionRoute> {}
