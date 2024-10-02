/**
 * 
 */
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
	
	
	/** Structures associated to the principal. */
    @ManyToMany(
            fetch=FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            }
    )
    @JoinTable(
            name="principal_structure",
            joinColumns = @JoinColumn(name= "id_principal"),
            inverseJoinColumns = @JoinColumn(name="id_structure")
            
    )
    private List<Structure> structures = new ArrayList<>();
	
		
}
