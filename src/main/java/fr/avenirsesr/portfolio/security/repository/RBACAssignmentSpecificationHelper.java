package fr.avenirsesr.portfolio.security.repository;

import java.util.Arrays;
import java.util.UUID;

import fr.avenirsesr.portfolio.security.model.*;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

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
			root.fetch(RBACAssignment_.principal, JoinType.LEFT);
			root.fetch(RBACAssignment_.role, JoinType.LEFT);
			root.fetch(RBACAssignment_.scope, JoinType.LEFT);
			root.fetch(RBACAssignment_.context, JoinType.LEFT);
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
	public static Specification<RBACAssignment> filterByPrincipalAndResources(String login, UUID... resourceIds) {
		return (Root<RBACAssignment> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {

			log.trace("filterByPrincipalAndResources, login: {}", login);
			log.trace("filterByPrincipalAndResources, resourceIds: {}", Arrays.toString(resourceIds));

			root.fetch(RBACAssignment_.principal, JoinType.LEFT);
			root.fetch(RBACAssignment_.role, JoinType.LEFT);

			Join<RBACAssignment, RBACScope> joinScope = root.join(RBACAssignment_.scope);
			Join<RBACScope, RBACResource> joinResource = joinScope.join(RBACScope_.resources);

			return criteriaBuilder.and(
			    filterByPrincipal(login).toPredicate(root, query, criteriaBuilder),
			    joinResource.get(RBACResource_.id).in((Object[]) resourceIds)
			);
		};
	}


	/**
	 * Specification to generate predicate to select the assignments associated to a
	 * principal, a list of resource id	and an execution context.
	 * @param login The login of the principal.
	 * @param executionContext the execution context used to filter valid assignments.
	 * @param resourceIds The id of the resources.
	 * @return The assignments for the principal, the resources and the context.
	 */
	public static Specification<RBACAssignment> filterByPrincipalContextAndResources(String login,
																					 RBACContext executionContext,
																					 UUID... resourceIds) {
		return (Root<RBACAssignment> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {

			log.trace("filterByPrincipalAndResources, login: {}", login);
			log.trace("filterByPrincipalAndResources, resourceIds: {}", Arrays.toString(resourceIds));

			root.fetch(RBACAssignment_.principal, JoinType.LEFT);
			root.fetch(RBACAssignment_.role, JoinType.LEFT);

			Join<RBACAssignment, RBACScope> joinScope = root.join(RBACAssignment_.scope);
			Join<RBACScope, RBACResource> joinResource = joinScope.join(RBACScope_.resources);
			Join<RBACAssignment, RBACContext> joinContext = root.join(RBACAssignment_.context, JoinType.LEFT);

			Predicate principalPredicate = filterByPrincipal(login).toPredicate(root, query, criteriaBuilder);
			Predicate resourcesPredicate = joinResource.get(RBACResource_.id).in((Object[]) resourceIds);


			// Filter by validity period of application context.
			Predicate contextDatePredicate = criteriaBuilder.and(
					criteriaBuilder.or(
							criteriaBuilder.isNull(joinContext.get(RBACContext_.validityStart)),
							criteriaBuilder.lessThanOrEqualTo(joinContext.get(RBACContext_.validityStart), executionContext.getEffectiveDate())
					),
					criteriaBuilder.or(
							criteriaBuilder.isNull(joinContext.get(RBACContext_.validityEnd)),
							criteriaBuilder.greaterThanOrEqualTo(joinContext.get(RBACContext_.validityEnd), executionContext.getEffectiveDate())
					)
			);

			// Structure filtering. No limitation of the structure in the application context or
			// all the structures are contained in the execution context.
			Predicate contextStructuresPredicate = criteriaBuilder.or(
					criteriaBuilder.isEmpty(joinContext.get(RBACContext_.structures)),
					joinContext.join(RBACContext_.structures).in(executionContext.getStructures())
			);

			return criteriaBuilder.and(
					principalPredicate,
					resourcesPredicate,
					contextDatePredicate,
					contextStructuresPredicate
			);
		};
	}
}
