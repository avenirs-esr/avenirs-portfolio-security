package fr.avenirsesr.portfolio.security.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Permission.
 * A permission is mainly a label and can be required to perform actions.
 */
@Data
@Entity
@Table(name="permission")
public class RBACPermission {
	
	/** Database id. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	/** Name of the permission.*/
	private String name;
	
	/** Description of the permission. */
	private String description;
}
