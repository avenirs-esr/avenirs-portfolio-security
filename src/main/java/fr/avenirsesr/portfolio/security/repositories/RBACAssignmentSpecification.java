package fr.avenirsesr.portfolio.security.repositories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import fr.avenirsesr.portfolio.security.models.Principal_;
import fr.avenirsesr.portfolio.security.models.RBACAssignment;
import fr.avenirsesr.portfolio.security.models.RBACAssignment_;
import fr.avenirsesr.portfolio.security.models.RBACResource;
import fr.avenirsesr.portfolio.security.models.RBACResource_;
import fr.avenirsesr.portfolio.security.models.RBACScope;
import fr.avenirsesr.portfolio.security.models.RBACScope_;
import fr.avenirsesr.portfolio.security.services.RBACActionService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

/**
 * Assignment specification for Assignment model.
 * Used to make queries based on predicates.
 */
public interface RBACAssignmentSpecification  {
	

	/**
	 * Specification to generate predicate to select the assignments associated to a principal.
	 * @param login The login used to select the assignments.
	 * @return The assignments for the principal.
	 */
	public static Specification<RBACAssignment> filterByPrincipal(String login) {
		return (Root<RBACAssignment> root, CriteriaQuery<?> query,  CriteriaBuilder criteriaBuilder) -> {
			return criteriaBuilder.equal(root.get(RBACAssignment_.principal).get(Principal_.login), login);
		};	
	}
	
	/**
	 * Specification to generate predicate to select the assignments associated to a principal and a list of resources.
	 * @param login The login of the principal.
	 * @param resourceIds The id of the resources.
	 * @return The assignments for the principal and the resources.
	 */

	public static Specification<RBACAssignment> filterByPrincipalAndResources(String login, Long[] resourceIds) {
		final Logger LOGGER = LoggerFactory.getLogger(RBACAssignmentSpecification.class);
		return (Root<RBACAssignment> root, CriteriaQuery<?> query,  CriteriaBuilder criteriaBuilder) -> {
//			List<Predicate> predicates = new ArrayList<>();
//			for(Long resourceId : resourceIds) {
//				
//				Subquery<Long> subquery = query.subquery(Long.class);
//		        Root<RBACScope> subqueryScope = subquery.from(RBACScope.class);
//		        Join<RBACScope, RBACResource> subqueryResource = subqueryScope.join(RBACScope_.resources);
//		        
//		        // Select the Student ID where one of their courses matches
//		        subquery.select(subqueryResource.get(RBACResource_.id)).where(
//		        		criteriaBuilder.equal(root.get(RBACAssignment_.scope).get(RBACScope_.resources));
//
//		        // Filter by Students that match one of the Students found in the subquery
//		        predicates.add(cb.in(student.get("id")).value(subquery));
//			}
			LOGGER.trace("filterByPrincipalAndResources, login: {}", login);
			LOGGER.trace("filterByPrincipalAndResources, resourceIds: {}", Arrays.toString(resourceIds));
			
			Join<RBACAssignment, RBACScope> joinScope = root.join(RBACAssignment_.scope);
			Join<RBACScope, RBACResource> joinResource =  joinScope.join(RBACScope_.resources);
			
			final Subquery<RBACScope> subquery = query.subquery(RBACScope.class);
			final Root<RBACScope> subroot = subquery.from(RBACScope.class);
			
			
			
			
			return joinResource.get(RBACResource_.id).in((Object[])resourceIds);
			
			
			
			
			
//			return criteriaBuilder.and(
//					filterByPrincipal(login).toPredicate(root, query, criteriaBuilder),
//					joinResource.get(RBACResource_.id).in((Object[])resourceIds)
//					
//				);
		};	
	}
}
