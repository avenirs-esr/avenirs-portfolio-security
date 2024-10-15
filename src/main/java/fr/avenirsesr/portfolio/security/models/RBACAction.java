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
 * Context in the RBAC system.
 * A context is used to limit an assignment with a period of validity and an establishment.
 */
@Data
@Accessors(chain=true)
@Entity
@Table(name="action")
public class RBACAction {
	
	/** Database Id. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	/** Name associated to the context.*/
	private String name;
	
	/** Description of the context. */
	private String description;
	
	/** Permissions associated to the scope. */
	@ManyToMany(
			fetch=FetchType.LAZY,
			cascade = {
					CascadeType.PERSIST,
					CascadeType.MERGE
			}
	)
	@JoinTable(
			name="action_permission",
			joinColumns = @JoinColumn(name= "id_action"),
			inverseJoinColumns = @JoinColumn(name="id_permission")
			
	)
	private List<RBACPermission> permissions = new ArrayList<>();

}
