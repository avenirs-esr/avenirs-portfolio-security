package fr.avenirsesr.portfolio.security.repository;

import fr.avenirsesr.portfolio.security.model.RBACResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;

/**
 * 
 * <h1>ResourceSpecificationHelper</h1>
 * <p>
 * Description: Specification helper used to filter Resources.
 * </p>
 * 
 * <h2>Version:</h2>
 * 1.0
 * 
 * <h2>Author:</h2>
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 * 17 Oct 2024
 */

@Slf4j
public abstract class RBACResourceSpecificationHelper {
		
	/**
	 * Generates a Specification to filter Resources from a list of  ids.
	 * @param ids The ids of the resources.
	 * @return The Specification .
	 */
	public static Specification<RBACResource> filterByIds(Long...ids) {
		if (log.isTraceEnabled()) {
            log.trace("filterByIds ids: {}", Arrays.toString(ids));
		}
		return (root, query, criteriaBuilder) -> root.get("id").in(Arrays.asList(ids));
	}
}
