package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACRole;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACRoleRepository;
import fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.mapper.RBACRoleMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class RBACRoleDatabaseRepository implements RBACRoleRepository {

  private final RBACRoleJpaRepository rbacRoleJpaRepository;
  private final RBACRoleMapper roleMapper;

  @Override
  @Transactional
  public Optional<RBACRole> findById(UUID roleId) {
    return rbacRoleJpaRepository.findById(roleId).map(roleMapper::toDomain);
  }

  @Override
  @Transactional
  public Optional<RBACRole> findByName(String roleName) {
    return rbacRoleJpaRepository.findByName(roleName).map(roleMapper::toDomain);
  }

  @Override
  @Transactional
  public List<RBACRole> findAll() {
    return rbacRoleJpaRepository.findAll().stream().map(roleMapper::toDomain).toList();
  }

  @Override
  @Transactional
  public RBACRole save(RBACRole role) {
    return roleMapper.toDomain(rbacRoleJpaRepository.save(roleMapper.fromDomain(role)));
  }

  @Override
  @Transactional
  public void deleteById(UUID roleId) {
    rbacRoleJpaRepository.deleteById(roleId);
  }
}
