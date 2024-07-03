package fr.avenirsesr.portfolio.security.services;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	public RBACAction getAction(final Long actionId){
		LOGGER.trace("getAction");
		return this.actionRepository.findById(actionId).get();
	}
	
	/**
	 * Find an action by its name.
	 * @param name The name of the action to retrieve.
	 * @return The action if found (Optional).
	 */
	public Optional<RBACAction> getAction(final String name) {
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
	
		
}
