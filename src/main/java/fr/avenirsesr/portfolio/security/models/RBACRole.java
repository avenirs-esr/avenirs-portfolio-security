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

/**
 * Role in the RBAC system.
 * A role is associated to one or several permissions.
 */
@Data
@Entity
@Table(name = "role")
public class RBACRole {

	/** Database id. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	/** Name of the Role. */
	private String name;

	/** Descriptions of the Role. */
	private String description;
	
	/** Permission associated to the role. */
	@ManyToMany(
			fetch=FetchType.LAZY,
			cascade = {
					CascadeType.PERSIST,
					CascadeType.MERGE
			}
	)
	@JoinTable(
			name="role_permission",
			joinColumns = @JoinColumn(name= "id_role"),
			inverseJoinColumns = @JoinColumn(name="id_permission")
			
	)
	private List<RBACPermission> permissions = new ArrayList<>();

}