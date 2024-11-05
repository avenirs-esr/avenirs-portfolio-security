package fr.avenirsesr.portfolio.security.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import fr.avenirsesr.portfolio.security.model.RBACResource_;
import fr.avenirsesr.portfolio.security.model.RBACScope;
import fr.avenirsesr.portfolio.security.model.RBACScope_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.Arrays;
import java.util.UUID;

/**
 * 
 * <h1>RBACScopeSpecification</h1>
 * <p>
 * Description:  Specification used to filter RBAC Scopes.
 * </p>
 * 
 * <h2>Version:</h2>
 * 1.0
 * 
 * <h2>Author:</h2>
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 * 1 Oct 2024
 */

@Slf4j
public abstract class RBACScopeSpecificationHelper {
		
	/**
	 * Specification to filter RBACScope from a list of resource ids.
	 * @param resourceIds The Resource ids.
	 * @return The Specification of RBACScope.
	 */
	public static Specification<RBACScope> filterByResources(UUID...resourceIds) {
		log.trace("filterByResources resourceIds: {}", Arrays.toString(resourceIds));
		return (Root<RBACScope> root, CriteriaQuery<?> query,  CriteriaBuilder criteriaBuilder) -> {
			return root.join(RBACScope_.resources).get(RBACResource_.id).in((Object[]) resourceIds);
		};	
	}
}
