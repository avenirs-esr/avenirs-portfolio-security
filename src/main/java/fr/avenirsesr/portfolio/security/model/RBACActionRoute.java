package fr.avenirsesr.portfolio.security.model;

import lombok.experimental.Accessors;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

/**
 * Represents the association between an HTTP route (URI + HTTP method) and an RBAC Action.
 */
@Data
@Accessors(chain=true)
@Entity
@Table(name="action_route")
public class RBACActionRoute {
	
	/** Database Id. */
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	/** URI associated to the route. */
	private String uri;
	
	/** method associated to the route.*/
	private String method;

	/** Action associated to the route. */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_action", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private RBACAction action;

}
