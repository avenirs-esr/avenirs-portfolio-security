/**
 * 
 */
package fr.avenirsesr.portfolio.security.services;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.avenirsesr.portfolio.security.model.RBACContext;
import fr.avenirsesr.portfolio.security.repository.RBACContextRepository;
import org.springframework.transaction.annotation.Transactional;

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

@Slf4j
@Service
public class RBACContextService {
   
  @Autowired
  private RBACContextRepository contextRepository;
 
  /**
   * Gives all the contexts.
   * @return All contexts.
   */
  @Transactional(readOnly = true)
  public List<RBACContext> getAllContexts() {
      log.trace("getAllContexts");
      return this.contextRepository.findAll();
  }
    /**
     * Gives a specific context by its id.
     * @param contextId The id of the context to retrieve.
     * @return The Optional of context with id contextId.
     */
    @Transactional(readOnly = true)
    public Optional<RBACContext> getContextById(final Long contextId){
        log.trace("getContextById contextId: {}", contextId);
        return contextRepository.findById(contextId);
    }
  /**
   * Creates a context.
   * @param context The context to create.
   * @return The saved context.
   */
  @Transactional
  public RBACContext createContext(RBACContext context) {
      log.trace("createContext, context: {}", context);
      return this.contextRepository.save(context);
  }
    /**
     * Updates a context.
     * @param context The context to update.
     * @return The updated context.
     */
    @Transactional
    public RBACContext updateContext(RBACContext context) {
        log.trace("updateContext, context: {}", context);
        return this.contextRepository.findById(context.getId())
                .map(storedContext -> {
                    if (!storedContext.equals(context)) {
                        storedContext.setValidityStart(context.getValidityStart());
                        storedContext.setValidityEnd(context.getValidityEnd());
                        storedContext.setStructures(context.getStructures());
                    }
                    return storedContext;
                })
                .orElseThrow(() -> new EntityNotFoundException("Entity not found with ID: " + context.getId()));
    }
  /**
   * Deletes a context by id.
   * @param contextId The id of the context to delete.
   */
  @Transactional
  public void deleteContext(Long contextId) {
      log.trace("deleteAssignment, contextId: {}", contextId);
       this.contextRepository.deleteById(contextId);
  }

}
