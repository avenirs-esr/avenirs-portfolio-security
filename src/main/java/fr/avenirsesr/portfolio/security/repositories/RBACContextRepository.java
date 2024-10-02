package fr.avenirsesr.portfolio.security.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import fr.avenirsesr.portfolio.security.models.RBACContext;

/**
 * 
 * <h1>RBACContextRepository</h1>
 * <p>
 * Description:  Repository for the RBACContext used as application context (restriction on assignments).
 * 
 * </p>
 * 
 * <h2>Version:</h2>
 * 1.0
 * 
 * <h2>Author:</h2>
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 * 1 Oct 2024
 */
public interface RBACContextRepository extends JpaRepository<RBACContext, Long>, JpaSpecificationExecutor<RBACContext>   {
	
}
