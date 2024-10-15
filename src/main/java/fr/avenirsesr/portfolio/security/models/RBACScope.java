package fr.avenirsesr.portfolio.security.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Scope in the RBAC system.
 * A scope is used to determine the resources involved in a Role assignment.
 */
@Data
@Accessors(chain=true)
@Entity
@Table(name="scope")
public class RBACScope {
	
	/** Database id. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	/** Name of the scope. */
	private String name;
	
	/** Resources associated to the scope. */
	@ManyToMany(
			fetch=FetchType.LAZY,
			cascade = {
					CascadeType.PERSIST,
					CascadeType.MERGE
			}
	)
	@JoinTable(
			name="scope_resource",
			joinColumns = @JoinColumn(name= "id_scope"),
			inverseJoinColumns = @JoinColumn(name="id_resource")
			
	)
	private List<RBACResource> resources = new ArrayList<>();
	
}
