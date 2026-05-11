package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACResource;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACResourceRepository;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.mapper.RBACResourceMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class RBACResourceDatabaseRepository implements RBACResourceRepository {

  private final RBACResourceJpaRepository rbacResourceJpaRepository;
  private final RBACResourceMapper resourceMapper;

  @Override
  @Transactional
  public List<RBACResource> findAllByIds(List<UUID> ids) {
    return rbacResourceJpaRepository.findAllById(ids).stream()
        .map(resourceMapper::toDomain)
        .toList();
  }

  @Override
  @Transactional
  public Optional<RBACResource> findById(UUID resourceId) {
    return rbacResourceJpaRepository.findById(resourceId).map(resourceMapper::toDomain);
  }

  @Override
  @Transactional
  public List<RBACResource> findAll() {
    return rbacResourceJpaRepository.findAll().stream().map(resourceMapper::toDomain).toList();
  }

  @Override
  @Transactional
  public RBACResource save(RBACResource resource) {
    return resourceMapper.toDomain(
        rbacResourceJpaRepository.save(resourceMapper.fromDomain(resource)));
  }

  @Override
  @Transactional
  public void deleteById(UUID resourceId) {
    rbacResourceJpaRepository.deleteById(resourceId);
  }
}
