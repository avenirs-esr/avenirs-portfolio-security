package fr.avenirsesr.portfolio.security.repositories;

import org.springframework.data.jpa.domain.Specification;

import fr.avenirsesr.portfolio.security.models.RBACAssignment;
import fr.avenirsesr.portfolio.security.models.RBACAssignment_;
import fr.avenirsesr.portfolio.security.models.Principal_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public interface RBACAssignmentSpecification  {
	
	public static Specification<RBACAssignment> assignmentHasPrincipalWithLogin(String login) {
		return (Root<RBACAssignment> root, CriteriaQuery<?> query,  CriteriaBuilder criteriaBuilder) -> {
			return criteriaBuilder.equal(root.get(RBACAssignment_.principal).get(Principal_.login), login);
		};	
	}
}
