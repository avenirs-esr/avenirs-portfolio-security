package fr.avenirsesr.portfolio.security.accesscontrol.domain.service;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.exception.AccessControlNotFoundException;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACContext;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.input.RBACContextService;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACContextRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class RBACContextServiceImpl implements RBACContextService {

  private final RBACContextRepository contextRepository;

  @Override
  public List<RBACContext> getAllContexts() {
    log.trace("getAllContexts");
    return contextRepository.findAll();
  }

  @Override
  public Optional<RBACContext> getContextById(UUID contextId) {
    log.trace("getContextById, contextId: {}", contextId);
    return contextRepository.findById(contextId);
  }

  @Override
  public RBACContext createContext(RBACContext context) {
    log.trace("createContext, context: {}", context);
    return contextRepository.save(context);
  }

  @Override
  public RBACContext updateContext(RBACContext context) {
    log.trace("updateContext, context: {}", context);

    contextRepository
        .findById(context.id())
        .orElseThrow(() -> AccessControlNotFoundException.context(context.id()));

    return contextRepository.save(context);
  }

  @Override
  public void deleteContext(UUID contextId) {
    log.trace("deleteContext, contextId: {}", contextId);
    contextRepository.deleteById(contextId);
  }
}
