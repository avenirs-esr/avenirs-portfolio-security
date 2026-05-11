package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACAction;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACActionRepository;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.mapper.RBACActionMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class RBACActionDatabaseRepository implements RBACActionRepository {

  private final RBACActionJpaRepository actionJpaRepository;
  private final RBACActionMapper actionMapper;

  @Override
  @Transactional
  public Optional<RBACAction> findById(UUID actionId) {
    return actionJpaRepository.findById(actionId).map(actionMapper::toDomain);
  }

  @Override
  @Transactional
  public Optional<RBACAction> findByName(String name) {
    return actionJpaRepository.findByName(name).map(actionMapper::toDomain);
  }

  @Override
  @Transactional
  public List<RBACAction> findAll() {
    return actionJpaRepository.findAll().stream().map(actionMapper::toDomain).toList();
  }
}
