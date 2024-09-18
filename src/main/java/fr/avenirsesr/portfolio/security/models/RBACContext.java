package fr.avenirsesr.portfolio.security.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Context in the RBAC system.
 * A context is used to limit an assignment with a period of validity and an establishment.
 */
@Data
@Entity
@Table(name="context")
public class RBACContext {
	
	/** Database Id. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	/** Start of  validity. */
	@Column(columnDefinition="TIMESTAMP")
	private LocalDateTime validity_start;

	/** End of the validity. */
	@Column(columnDefinition="TIMESTAMP")
	private LocalDateTime validity_end;
}
