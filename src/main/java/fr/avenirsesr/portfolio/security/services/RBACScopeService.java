package fr.avenirsesr.portfolio.security.services;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import fr.avenirsesr.portfolio.security.models.RBACScope;
import fr.avenirsesr.portfolio.security.repositories.RBACScopeRepository;


/**
 * Scope Service
 */

@Slf4j
@Service
public class RBACScopeService {
	
	/** Scope repository. */
	@Autowired
	private RBACScopeRepository scopeRepository;
	
	/**
	 * Gives all the scopes associated to a predicate.
	 * 
	 * @param specification The specification used to filter the scopes.
	 * @return The filtered scopes.
	 */
	public List<RBACScope> getAllScopesBySpecification(Specification<RBACScope> specification) {
		
		log.trace("getAllScopesByPredicate, specification: {}", specification);
		return scopeRepository.findAll(specification);
	}
	
		
}
