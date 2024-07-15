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
	 * Specification to generate predicate to select the assignments associated to a principal and a list of resources.
	 * @param login The login of the principal.
	 * @param resourceIds The id of the resources.
	 * @return The assignments for the principal and the resources.
	 */

	public static Specification<RBACScope> filterByResources(Long...resourceIds) {
		return (Root<RBACScope> root, CriteriaQuery<?> query,  CriteriaBuilder criteriaBuilder) -> {
			
//			List<Predicate> predicates = new ArrayList<>();
//			for(Long resourceId : resourceIds) {
//				predicates.add(criteriaBuilder.equal(root.join(RBACScope_.resources).get(RBACResource_.id), resourceId));
//          
//			}
			return root.join(RBACScope_.resources).get(RBACResource_.id).in((Object[]) resourceIds);
			//return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
			
		};	
//		return (Root<RBACScope> root, CriteriaQuery<?> query,  CriteriaBuilder criteriaBuilder) -> {
//			
//			List<Predicate> predicates = new ArrayList<>();
//			for(Long resourceId : resourceIds) {
//				predicates.add(criteriaBuilder.equal(root.join(RBACScope_.resources).get(RBACResource_.id), resourceId));
//				
//			}
//			return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
//			
//		};	
	}
}
