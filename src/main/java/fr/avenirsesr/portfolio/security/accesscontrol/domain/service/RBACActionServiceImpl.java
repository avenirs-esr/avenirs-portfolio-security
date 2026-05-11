package fr.avenirsesr.portfolio.security.accesscontrol.domain.service;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACAction;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.input.RBACActionService;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACActionRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class RBACActionServiceImpl implements RBACActionService {

  private final RBACActionRepository actionRepository;

  @Override
  public Optional<RBACAction> getActionById(UUID actionId) {
    log.trace("getActionById, actionId: {}", actionId);
    return actionRepository.findById(actionId);
  }

  @Override
  public Optional<RBACAction> getActionByName(String name) {
    log.trace("getActionByName, name: {}", name);
    return actionRepository.findByName(name);
  }

  @Override
  public List<RBACAction> getAllActions() {
    log.trace("getAllActions");
    return actionRepository.findAll();
  }
}
