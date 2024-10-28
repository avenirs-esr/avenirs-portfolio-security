package fr.avenirsesr.portfolio.security.repositories;

import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import fr.avenirsesr.portfolio.security.model.Principal_;
import fr.avenirsesr.portfolio.security.model.RBACAssignment;
import fr.avenirsesr.portfolio.security.model.RBACAssignment_;
import fr.avenirsesr.portfolio.security.model.RBACResource;
import fr.avenirsesr.portfolio.security.model.RBACResource_;
import fr.avenirsesr.portfolio.security.model.RBACScope;
import fr.avenirsesr.portfolio.security.model.RBACScope_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;

/**
 * Assignment specification for Assignment model. Used to make queries based on
 * predicates.
 */
@Slf4j
public abstract class RBACAssignmentSpecificationHelper {

	/**
	 * Specification to generate predicate to select the assignments associated to a
	 * principal.
	 * 
	 * @param login The login used to select the assignments.
	 * @return The assignments for the principal.
	 */
	public static Specification<RBACAssignment> filterByPrincipal(String login) {
		return (Root<RBACAssignment> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
			return criteriaBuilder.equal(root.get(RBACAssignment_.principal).get(Principal_.login), login);
		};
	}

	/**
	 * Specification to generate predicate to select the assignments associated to a
	 * principal and a list of resource id	.
	 * 
	 * @param login       The login of the principal.
	 * @param resourceIds The id of the resources.
	 * @return The assignments for the principal and the resources.
	 */

	public static Specification<RBACAssignment> filterByPrincipalAndResources(String login, Long... resourceIds) {
		return (Root<RBACAssignment> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {

			log.trace("filterByPrincipalAndResources, login: {}", login);
			log.trace("filterByPrincipalAndResources, resourceIds: {}", Arrays.toString(resourceIds));

			Join<RBACAssignment, RBACScope> joinScope = root.join(RBACAssignment_.scope);
			Join<RBACScope, RBACResource> joinResource = joinScope.join(RBACScope_.resources);

			return criteriaBuilder.and(
			    filterByPrincipal(login).toPredicate(root, query, criteriaBuilder),
			    joinResource.get(RBACResource_.id).in((Object[]) resourceIds)
			);
		};
	}
}
