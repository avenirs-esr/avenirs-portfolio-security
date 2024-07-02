package fr.avenirsesr.portfolio.security.services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.avenirsesr.portfolio.security.models.RBACRole;
import fr.avenirsesr.portfolio.security.repositories.RBACRoleRepository;


/**
 * Role Service
 */
@Service
public class RBACRoleService {
	
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(RBACRoleService.class);
	
	@Autowired
	private RBACRoleRepository roleRepository;
	
	/**
	 * Gives a specific role.
	 * @param roleId The id of the role to retrieve.
	 * @return The role with id roleId.
	 */
	public RBACRole getRole(final Long roleId){
		LOGGER.trace("getRole");
		return this.roleRepository.findById(roleId).get();
	}
	
	/**
	 * Gives all the roles.
	 * @return All the roles.
	 */
	public Iterable<RBACRole> getAllRoles() {
		LOGGER.trace("getAllRoles");
		return this.roleRepository.findAll();
	}
	
	/**
	 * Creates a role.
	 * @param role The role to create.
	 * @return The new created role.
	 */
	public RBACRole createRole(RBACRole role) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("createRole, role: " + role);
		};
		return this.roleRepository.save(role);
	}
	
	/**
	 * Updates a role.
	 * @param role The role to update.
	 * @return The updated role.
	 */
	public RBACRole  updateRole(RBACRole role) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("updateRole, role: " + role);
		};
		RBACRole storedRole = this.roleRepository.findById(role.getId()).get();
		
		if (storedRole != null && ! storedRole.equals(role)) {
				storedRole.setName(role.getName());
				storedRole.setDescription(role.getDescription());
				return this.roleRepository.save(storedRole);
		}
		return storedRole;
	}
	
	/**
	 * Deletes a role
	 * @param roleId The id of the role to delete.
	 */
	public void deleteRole(Long roleId) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("deleteRole, roleId: " + roleId);
		};
		this.roleRepository.deleteById(roleId);
	}
	
	
}
