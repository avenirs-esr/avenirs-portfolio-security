package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model.RBACResourceEntity;
import java.util.Arrays;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 *
 * <h1>ResourceSpecificationHelper</h1>
 *
 * <p>Description: Specification helper used to filter Resources.
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
 * 17 Oct 2024
 */
@Slf4j
public abstract class RBACResourceSpecificationHelper {

  /**
   * Generates a Specification to filter Resources from a list of ids.
   *
   * @param ids The ids of the resources.
   * @return The Specification .
   */
  public static Specification<RBACResourceEntity> filterByIds(UUID... ids) {
    if (log.isTraceEnabled()) {
      log.trace("filterByIds ids: {}", Arrays.toString(ids));
    }
    return (root, query, criteriaBuilder) -> root.get("id").in(Arrays.asList(ids));
  }
}
