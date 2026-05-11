package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACScope;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACScopeRepository;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.mapper.RBACScopeMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class RBACScopeDatabaseRepository implements RBACScopeRepository {

  private final RBACScopeJpaRepository scopeJpaRepository;
  private final RBACScopeMapper scopeMapper;

  @Override
  @Transactional
  public Optional<RBACScope> findById(UUID scopeId) {
    return scopeJpaRepository.findById(scopeId).map(scopeMapper::toDomain);
  }

  @Override
  @Transactional
  public Optional<RBACScope> findByName(String scopeName) {
    return scopeJpaRepository.findByName(scopeName).map(scopeMapper::toDomain);
  }

  @Override
  @Transactional
  public List<RBACScope> findAll() {
    return scopeJpaRepository.findAll().stream().map(scopeMapper::toDomain).toList();
  }

  @Override
  @Transactional
  public RBACScope save(RBACScope scope) {
    return scopeMapper.toDomain(scopeJpaRepository.save(scopeMapper.fromDomain(scope)));
  }

  @Override
  @Transactional
  public void deleteById(UUID scopeId) {
    scopeJpaRepository.deleteById(scopeId);
  }
}
