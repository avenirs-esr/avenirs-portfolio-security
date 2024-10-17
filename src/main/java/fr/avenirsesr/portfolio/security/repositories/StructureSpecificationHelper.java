package fr.avenirsesr.portfolio.security.repositories;

import fr.avenirsesr.portfolio.security.models.RBACResource_;
import fr.avenirsesr.portfolio.security.models.RBACScope;
import fr.avenirsesr.portfolio.security.models.RBACScope_;
import fr.avenirsesr.portfolio.security.models.Structure;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;

/**
 * 
 * <h1>StructureSpecificationHelper</h1>
 * <p>
 * Description: Specification used to filter Structures.
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
public abstract class StructureSpecificationHelper {
		
	/**
	 * Specification to filter Structure from a list of  ids.
	 * @param ids The ids of the structures.
	 * @return The Specification .
	 */
	public static Specification<Structure> filterByIds(Long...ids) {
		if (log.isTraceEnabled()) {
			log.trace("filterByIds ids: {}", Arrays.toString(ids));
		}
		return (root, query, criteriaBuilder) -> root.get("id").in(Arrays.asList(ids));
	}
}
