package fr.avenirsesr.portfolio.security.service;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import fr.avenirsesr.portfolio.security.model.RBACScope;
import fr.avenirsesr.portfolio.security.repository.RBACScopeRepository;
import org.springframework.transaction.annotation.Transactional;


/**
 * <h1>RBACScopeService</h1>
 * <p>
 * <b>Description:</b> Handle the scope of an assignment. The scope is the object that contains the resources associated to an assignment.
 * </p>
 *
 * <h2>Version:</h2>
 * 1.0.0
 *
 * <h2>Author:</h2>
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 * 16/10/2024
 */

@Slf4j
@Service
public class RBACScopeService {
	
	/** Scope repository. */
	@Autowired
	private RBACScopeRepository scopeRepository;

	/**
	 * Gives a specific scope by its id.
	 * @param scopeId The id of the scope to retrieve.
	 * @return The Optional of scope with id scopeId.
	 */
	@Transactional(readOnly = true)
	public Optional<RBACScope> getScopeById(final Long scopeId){
		log.trace("getScopeById scopeId: {}", scopeId);
		return scopeRepository.findById(scopeId);
	}

	/**
	 * Gives a specific scope by its name.
	 * @param scopeName The name of the scope to retrieve.
	 * @return The Optional of scope with name scopeName.
	 */
	@Transactional(readOnly = true)
	public Optional<RBACScope> getScopeByName(final String scopeName) {
		log.trace("getScopeByName scopeName: {}", scopeName);
		return this.scopeRepository.findByName(scopeName);
	}

	/**
	 * Gives all the scopes.
	 * @return All the scopes.
	 */
	@Transactional(readOnly = true)
	public List<RBACScope> getAllScopes() {
		log.trace("getAllScopes");
		return this.scopeRepository.findAll();
	}


	/**
	 * Gives all the scopes associated to a predicate.
	 * 
	 * @param specification The specification used to filter the scopes.
	 * @return The filtered scopes.
	 */
	@Transactional(readOnly = true)
	public List<RBACScope> getAllScopesBySpecification(Specification<RBACScope> specification) {
		log.trace("getAllScopesByPredicate, specification: {}", specification);
		return scopeRepository.findAll(specification);
	}

	/**
	 * Creates a scope.
	 * @param scope The scope to create.
	 * @return The new created scope.
	 */
	@Transactional
	public RBACScope createScope(RBACScope scope) {
		log.trace("createScope, scope: {}", scope);
		return this.scopeRepository.save(scope);
	}

	/**
	 * Updates a scope.
	 * @param scope The scope to update.
	 * @return The updated scope.
	 */
	@Transactional
	public RBACScope  updateScope(RBACScope scope) {
		log.trace("updateScope, scope: {}", scope);
		return this.scopeRepository.findById(scope.getId())
				.map(storedScope -> {
					if (!storedScope.equals(scope)) {
						storedScope.setName(scope.getName());
						storedScope.setResources(scope.getResources());
					}
					return storedScope;
				})
				.orElseThrow(() -> new EntityNotFoundException("Entity not found with ID: " + scope.getId()));
	}

	/**
	 * Deletes a scope
	 * @param scopeId The id of the scope to delete.
	 */
	@Transactional
	public void deleteScope(Long scopeId) {
		log.trace("deleteScope, scopeId: {}", scopeId);
		this.scopeRepository.deleteById(scopeId);
	}


	
		
}
