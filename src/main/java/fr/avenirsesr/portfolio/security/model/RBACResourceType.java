package fr.avenirsesr.portfolio.security.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

/**
 * Resources type.
 * The type is used to determine if a resource is a portfolio, a SAE , etc.
 */
@Data
@Accessors(chain=true)
@Entity
@Table(name="resource_type")
public class RBACResourceType {
	
	/** Database id. */
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	/** Name of the resource type.*/
	@Column(unique=true)
	private String name;
	
	/** Description of the resource type. */
	private String description;
}
