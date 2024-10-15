package fr.avenirsesr.portfolio.security.services;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import fr.avenirsesr.portfolio.security.models.RBACActionRoute;
import fr.avenirsesr.portfolio.security.repositories.RBACActionRouteRepository;


/**
 * ActionRoute Service
 */

@Slf4j
@Service
public class RBACActionRouteService {
	
	@Autowired
	private RBACActionRouteRepository actionRouteRepository;
	
	/**
	 * Gives an action route associated to a predicate.
	 * 
	 * @param specification The specification used to filter the ActionRoute instances.
	 * @return The filtered instances.
	 */
	public Optional<RBACActionRoute> getAllActionRoutesBySpecification(Specification<RBACActionRoute> specification) {
		
		log.trace("getAllScopesByPredicate, specification: {}", specification);
		return actionRouteRepository.findOne(specification);
	}
		
}
