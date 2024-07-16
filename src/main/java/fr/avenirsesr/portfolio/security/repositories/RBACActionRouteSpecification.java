package fr.avenirsesr.portfolio.security.repositories;

import org.springframework.data.jpa.domain.Specification;

import fr.avenirsesr.portfolio.security.models.Principal_;
import fr.avenirsesr.portfolio.security.models.RBACActionRoute;
import fr.avenirsesr.portfolio.security.models.RBACActionRoute_;
import fr.avenirsesr.portfolio.security.models.RBACAction_;
import fr.avenirsesr.portfolio.security.models.RBACAssignment_;
import fr.avenirsesr.portfolio.security.models.RBACResource_;
import fr.avenirsesr.portfolio.security.models.RBACScope;
import fr.avenirsesr.portfolio.security.models.RBACScope_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

/**
 * Assignment specification for ActionRoute model.
 * Used to make queries based on predicates.
 */
public interface RBACActionRouteSpecification  {
	
	
	/**
	 * Specification to generate predicate to select the assignments associated to a principal and a list of resources.
	 * @param login The login of the principal.
	 * @param resourceIds The id of the resources.
	 * @return The assignments for the principal and the resources.
	 */

	public static Specification<RBACActionRoute> filterByURIAndMethod(String uri, String method) {
		return (Root<RBACActionRoute> root, CriteriaQuery<?> query,  CriteriaBuilder criteriaBuilder) -> {
			return criteriaBuilder.and(
					criteriaBuilder.equal(root.get(RBACActionRoute_.uri), uri),
					criteriaBuilder.equal(root.get(RBACActionRoute_.method), method)
					);
		};	
	}
}
