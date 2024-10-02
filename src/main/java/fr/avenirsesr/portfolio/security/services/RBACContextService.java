/**
 * 
 */
package fr.avenirsesr.portfolio.security.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.avenirsesr.portfolio.security.models.RBACContext;
import fr.avenirsesr.portfolio.security.models.Structure;
import fr.avenirsesr.portfolio.security.repositories.RBACContextRepository;

/**
 * <h1>RBACContextService</h1>
 * <p>
 * Description:  is used to retrieve Application Context.<br/>
 * <strong>Application Context:</strong> restriction on assignments. For instance a role can be assigned to a principal with a period of validity or for a given structure.<br/>
 * <strong>Execution Context:</strong> context computed at runtime,for instance the current date or the list of structures associated to the principal. <br/>
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
@Service
public class RBACContextService {
  /** Logger */
  private static final Logger LOGGER = LoggerFactory.getLogger(RBACAssignmentService.class);
  
  @Autowired
  private RBACContextRepository contextRepository;
 
  /**
   * Gives all the contexts.
   * @return All contexts.
   */
  public List<RBACContext> getAllContexts() {
      LOGGER.trace("getAllContexts");
      return this.contextRepository.findAll();
  }
  
  /**
   * Creates a context.
   * @param context The context to create.
   * @return The saved context.
   */
  public RBACContext createContext(RBACContext context) {
      LOGGER.trace("createContext, context: {}", context);
      return this.contextRepository.save(context);
  }
  
  /**
   * Deletes a context.
   * @param context The context to delete.
   */
  public void deleteAssignment(RBACContext context) {
      LOGGER.trace("deleteAssignment, context: {}", context);
      this.contextRepository.delete(context);
  }
  
  /**
   * Deletes a context by id.
   * @param contextId The id of the context to delete.
   */
  public void deleteContextById(Long contextId) {
      LOGGER.trace("deleteAssignment, contextId: {}" + contextId);
       
      
       this.contextRepository.deleteById(contextId);
  }

}
