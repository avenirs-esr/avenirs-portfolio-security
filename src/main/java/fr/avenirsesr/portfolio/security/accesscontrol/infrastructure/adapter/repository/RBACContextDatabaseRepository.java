package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACContext;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACContextRepository;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.mapper.RBACContextMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class RBACContextDatabaseRepository implements RBACContextRepository {

  private final RBACContextJpaRepository contextJpaRepository;
  private final RBACContextMapper contextMapper;

  @Override
  @Transactional
  public List<RBACContext> findAll() {
    return contextJpaRepository.findAll().stream().map(contextMapper::toDomain).toList();
  }

  @Override
  @Transactional
  public Optional<RBACContext> findById(UUID contextId) {
    return contextJpaRepository.findById(contextId).map(contextMapper::toDomain);
  }

  @Override
  @Transactional
  public RBACContext save(RBACContext context) {
    return contextMapper.toDomain(contextJpaRepository.save(contextMapper.fromDomain(context)));
  }

  @Override
  @Transactional
  public void deleteById(UUID contextId) {
    contextJpaRepository.deleteById(contextId);
  }
}
