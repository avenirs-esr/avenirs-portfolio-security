/**
 * 
 */
package fr.avenirsesr.portfolio.security.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Principal in the RBAC system.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="principal")
public class Principal {
	
	/** Database Id. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	/** Login of the user. */
	private String login;
	
	public Principal(Long id) {
		this.id = id;
	}
	
	public Principal(String login) {
		this.login = login;
	}
	
		
}
