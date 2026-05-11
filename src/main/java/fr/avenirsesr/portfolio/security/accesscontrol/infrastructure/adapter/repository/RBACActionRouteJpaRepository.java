package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACActionRouteEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/** Repository for RBACActionRoute. */
public interface RBACActionRouteJpaRepository
    extends JpaRepository<RBACActionRouteEntity, UUID>,
        JpaSpecificationExecutor<RBACActionRouteEntity> {
  Optional<RBACActionRouteEntity> findByUriAndMethod(String uri, String method);
}
