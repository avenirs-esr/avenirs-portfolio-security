package fr.avenirsesr.portfolio.security.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Context in the RBAC system.
 * A context is used to limit an assignment with a period of validity and an establishment.
 */
@Data
@Accessors(chain=true)
@Entity
@Table(name="context")
public class RBACContext {
	
	/** Database Id. */
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	/** Start of  validity. */
	@Column(columnDefinition="TIMESTAMP")
	private LocalDateTime validityStart;

	/** End of the validity. */
	@Column(columnDefinition="TIMESTAMP")
	private LocalDateTime validityEnd;
	
	/** Effective date. */
	@Transient
	private LocalDateTime effectiveDate = LocalDateTime.now(); 
	
	/** Structures associated to the context. */
    @ManyToMany(
            fetch=FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            }
    )
    @JoinTable(
            name="context_structure",
            joinColumns = @JoinColumn(name= "id_context"),
            inverseJoinColumns = @JoinColumn(name="id_structure")
            
    )
    private Set<Structure> structures = new HashSet<>();
}
