package fr.avenirsesr.portfolio.security.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Resources selector.
 * This class is used to determine one or several resources involved in a Role assignment.
 */
@Data
@Entity
@Table(name="resource")
public class RBACResource {
	
	/** Database id. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	/** Selector for one or several resources.*/
	private String selector;
	
	/** Type associated to the rseource. */
	@ManyToOne()
	@JoinColumn(name="id_resource_type")
	private RBACResourceType resourceType;
}