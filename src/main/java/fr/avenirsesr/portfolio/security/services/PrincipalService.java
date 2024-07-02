package fr.avenirsesr.portfolio.security.services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.avenirsesr.portfolio.security.models.Principal;
import fr.avenirsesr.portfolio.security.repositories.PrincipalRepository;


/**
 * Service for Principal.
 */
@Service
public class PrincipalService {
	
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(PrincipalService.class);
	
	@Autowired
	private PrincipalRepository principalRepository;
	
	
	/**
	 * Gives all the principals.
	 * @return An iterable over all principals.
	 */
	public Iterable<Principal> getAllPrincipals() {
		LOGGER.trace("getAllPrincipals");
		return this.principalRepository.findAll();
	}
	
//	/**
//	 * Creates a role.
//	 * @param role The role to create.
//	 * @return The new created role.
//	 */
//	public Role createRole(Role role) {
//		return this.roleRepository.save(role);
//	}
	
	/**
	 * Updates a role.
	 * @param role The role to update.
	 * @return The updated role.
	 */
//	public Role  updateRole(Role role) {
//		
//		Role storedRole = this.roleRepository.findById(role.getId()).get();
//		
//		if (storedRole != null && ! storedRole.equals(role)) {
//				storedRole.setName(role.getName());
//				storedRole.setDescription(role.getDescription());
//				return this.roleRepository.save(storedRole);
//		}
//		return storedRole;
//	}
	
	/**
	 * Deletes a role
	 * @param id The id of the role to delete.
	 */
//	public void deleteRole(Long id) {
//		this.roleRepository.deleteById(id);
//	}
	
	
}
