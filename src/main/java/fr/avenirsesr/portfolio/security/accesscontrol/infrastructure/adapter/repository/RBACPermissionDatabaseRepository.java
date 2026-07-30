package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACPermission;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACPermissionRepository;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.mapper.RBACPermissionMapper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class RBACPermissionDatabaseRepository implements RBACPermissionRepository {

  private final RBACPermissionJpaRepository rbacPermissionJpaRepository;
  private final RBACPermissionMapper permissionMapper;

  @Override
  @Transactional
  public Optional<RBACPermission> findByName(String name) {
    return rbacPermissionJpaRepository.findByName(name).map(permissionMapper::toDomain);
  }

  @Override
  @Transactional
  public List<RBACPermission> findAll() {
    return rbacPermissionJpaRepository.findAll().stream().map(permissionMapper::toDomain).toList();
  }
}
