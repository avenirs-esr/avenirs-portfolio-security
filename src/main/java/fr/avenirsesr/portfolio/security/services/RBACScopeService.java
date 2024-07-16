package fr.avenirsesr.portfolio.security.services;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import fr.avenirsesr.portfolio.security.models.RBACScope;
import fr.avenirsesr.portfolio.security.repositories.RBACScopeRepository;


/**
 * Scope Service
 */
@Service
public class RBACScopeService {
	
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(RBACScopeService.class);
	
	/** Scope repository. */
	@Autowired
	private RBACScopeRepository scopeRepository;
	
	/**
	 * Gives all the scopes associated to a predicate.
	 * 
	 * @param specification The specification of the predicate.
	 * @return The scopes.
	 */
	public List<RBACScope> getAllScopesByPredicate(Specification<RBACScope> specification) {
		
		LOGGER.trace("getAllScopesByPredicate, specification: {}" + specification);
		return scopeRepository.findAll(specification);
	}
	
		
}
