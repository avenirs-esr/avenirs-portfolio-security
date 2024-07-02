package fr.avenirsesr.portfolio.security.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Resources type.
 * The tyoe is used to determine if a resource is a portfolio, a SAE , etc.
 */
@Data
@Entity
@Table(name="resource_type")
public class RBACResourceType {
	
	/** Database id. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	/** Name of the resource type.*/
	@Column(unique=true)
	private String name;
	
	/** Description of the resource type. */
	private String description;
}
