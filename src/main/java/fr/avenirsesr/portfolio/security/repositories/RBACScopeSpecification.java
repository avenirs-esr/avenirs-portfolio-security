package fr.avenirsesr.portfolio.security.repositories;

import org.springframework.data.jpa.domain.Specification;

import fr.avenirsesr.portfolio.security.models.RBACResource_;
import fr.avenirsesr.portfolio.security.models.RBACScope;
import fr.avenirsesr.portfolio.security.models.RBACScope_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

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
public interface RBACScopeSpecification  {
		
	/**
	 * Specification to filter RBACScope from a list of resource ids.
	 * @param resourceIds The Resource ids.
	 * @return The Specification of RBACScope.
	 */
	public static Specification<RBACScope> filterByResources(Long...resourceIds) {
		return (Root<RBACScope> root, CriteriaQuery<?> query,  CriteriaBuilder criteriaBuilder) -> {
			return root.join(RBACScope_.resources).get(RBACResource_.id).in((Object[]) resourceIds);
		};	
	}
}
