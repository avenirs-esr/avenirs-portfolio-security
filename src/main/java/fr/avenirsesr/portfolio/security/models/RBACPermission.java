package fr.avenirsesr.portfolio.security.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Permission.
 * A permission is mainly a label and can be required to perform actions.
 */
@Data
@Accessors(chain=true)
@Entity
@Table(name="permission")
@NoArgsConstructor
@AllArgsConstructor
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
