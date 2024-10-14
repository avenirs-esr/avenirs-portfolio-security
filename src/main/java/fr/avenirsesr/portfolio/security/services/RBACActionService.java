package fr.avenirsesr.portfolio.security.services;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import fr.avenirsesr.portfolio.security.models.RBACAction;
import fr.avenirsesr.portfolio.security.repositories.RBACActionRepository;


/**
 * Action Service
 */
@Service
public class RBACActionService {
	
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(RBACActionService.class);
	
	@Autowired
	private RBACActionRepository actionRepository;
	
	/**
	 * Gives a specific action.
	 * @param actionId The id of the role to retrieve.
	 * @return The action with id actionId.
	 */
	public Optional<RBACAction> getActionById(final Long actionId){
		LOGGER.trace("getAction");
		return this.actionRepository.findById(actionId);
	}
	
	/**
	 * Find an action by its name.
	 * @param name The name of the action to retrieve.
	 * @return The action if found (Optional).
	 */
	public Optional<RBACAction> getActionByName(final String name) {
		return this.actionRepository.findByName(name);
	}
	
	/**
	 * Gives all the actions.
	 * @return All the action.
	 */
	public List<RBACAction> getAllActions() {
		LOGGER.trace("getAllActions");
		return this.actionRepository.findAll();
	}
	
	/**
	 * Gives all the actions associated to a specification.
	 * 
	 * @param specification The specification of the used to filter the actions.
	 * @return The filtered instances.
	 */
		public List<RBACAction> getAllActionsBySpecification(Specification<RBACAction> specification) {
		
		LOGGER.trace("getAllActionsByPredicate, specification: {}" + specification);
		return actionRepository.findAll(specification);
	}
	
	
		
}
