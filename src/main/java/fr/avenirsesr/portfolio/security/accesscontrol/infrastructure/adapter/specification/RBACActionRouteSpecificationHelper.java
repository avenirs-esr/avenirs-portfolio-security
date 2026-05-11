package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACActionRouteEntity;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACActionRouteEntity_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

/** ActionRoute specification helper. */
@Slf4j
public abstract class RBACActionRouteSpecificationHelper {

  public static Specification<RBACActionRouteEntity> filterByURIAndMethod(
      String uri, String method) {
    return (Root<RBACActionRouteEntity> root,
        CriteriaQuery<?> query,
        CriteriaBuilder criteriaBuilder) -> {
      log.trace("filterByURIAndMethod, uri: {}", uri);
      log.trace("filterByURIAndMethod, method: {}", method);

      String lcMethod = method == null ? null : method.toLowerCase();

      return criteriaBuilder.and(
          criteriaBuilder.equal(root.get(RBACActionRouteEntity_.uri), uri),
          criteriaBuilder.equal(
              criteriaBuilder.lower(root.get(RBACActionRouteEntity_.method)), lcMethod));
    };
  }
}
