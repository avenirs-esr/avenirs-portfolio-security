package fr.avenirsesr.portfolio.security.repositories;

import org.springframework.data.jpa.domain.Specification;

import fr.avenirsesr.portfolio.security.models.RBACResource_;
import fr.avenirsesr.portfolio.security.models.RBACScope;
import fr.avenirsesr.portfolio.security.models.RBACScope_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

/**
 * Assignment specification for Assignment model.
 * Used to make queries based on predicates.
 */
public interface RBACScopeSpecification  {
		
	/**
	 * Specification to generate predicate to select the RBACScope associated to a list of resource ids.
	 * @param resourceIds The resource ids.
	 * @return The specification of RBACScope.
	 */
	public static Specification<RBACScope> filterByResources(Long...resourceIds) {
		return (Root<RBACScope> root, CriteriaQuery<?> query,  CriteriaBuilder criteriaBuilder) -> {
			return root.join(RBACScope_.resources).get(RBACResource_.id).in((Object[]) resourceIds);
		};	
	}
}
