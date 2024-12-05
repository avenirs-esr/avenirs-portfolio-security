package fr.avenirsesr.portfolio.security.repository;

import fr.avenirsesr.portfolio.security.model.Structure;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.UUID;

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
	 * Specification to filter Structures from a list of  ids.
	 * @param ids The ids of the structures.
	 * @return The Specification .
	 */
	public static Specification<Structure> filterByIds(UUID...ids) {
		if (log.isTraceEnabled()) {
			log.trace("filterByIds ids: {}", Arrays.toString(ids));
		}
		return (root, query, criteriaBuilder) -> root.get("id").in(Arrays.asList(ids));
	}
}
