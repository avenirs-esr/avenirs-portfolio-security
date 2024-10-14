package fr.avenirsesr.portfolio.security.services;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import fr.avenirsesr.portfolio.security.models.RBACActionRoute;
import fr.avenirsesr.portfolio.security.repositories.RBACActionRouteRepository;


/**
 * ActionRoute Service
 */
@Service
public class RBACActionRouteService {
	
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(RBACActionRouteService.class);
	
	@Autowired
	private RBACActionRouteRepository actionRouteRepository;
	
	/**
	 * Gives an action route associated to a predicate.
	 * 
	 * @param specification The specification used to filter the ActionRoute instances.
	 * @return The filtered instances.
	 */
	public Optional<RBACActionRoute> getAllActionRoutesBySpecification(Specification<RBACActionRoute> specification) {
		
		LOGGER.trace("getAllScopesByPredicate, specification: {}", specification);
		return actionRouteRepository.findOne(specification);
	}
		
}
