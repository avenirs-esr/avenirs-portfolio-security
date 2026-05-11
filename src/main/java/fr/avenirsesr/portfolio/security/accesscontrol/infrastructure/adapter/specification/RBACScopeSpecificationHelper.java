package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACResourceEntity_;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACScopeEntity;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACScopeEntity_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.Arrays;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

/** Specification helper used to filter RBAC scopes. */
@Slf4j
public abstract class RBACScopeSpecificationHelper {

  public static Specification<RBACScopeEntity> filterByResources(UUID... resourceIds) {
    log.trace("filterByResources resourceIds: {}", Arrays.toString(resourceIds));

    return (Root<RBACScopeEntity> root,
        CriteriaQuery<?> query,
        CriteriaBuilder criteriaBuilder) -> {
      query.distinct(true);

      return root.join(RBACScopeEntity_.resources)
          .get(RBACResourceEntity_.id)
          .in((Object[]) resourceIds);
    };
  }
}
