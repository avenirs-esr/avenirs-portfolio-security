package fr.avenirsesr.portfolio.security.accesscontrol.domain.service;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.exception.AccessControlNotFoundException;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACAssignment;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACRole;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.input.RBACRoleService;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACAssignmentRepository;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACRoleRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class RBACRoleServiceImpl implements RBACRoleService {

  private final RBACRoleRepository roleRepository;
  private final RBACAssignmentRepository assignementRepository;

  @Override
  public Optional<RBACRole> getRoleById(UUID roleId) {
    log.trace("getRoleById roleId: {}", roleId);
    return roleRepository.findById(roleId);
  }

  @Override
  public Optional<RBACRole> getRoleByName(String roleName) {
    log.trace("getRoleByName roleName: {}", roleName);
    return roleRepository.findByName(roleName);
  }

  @Override
  public List<RBACRole> getAllRoles() {
    log.trace("getAllRoles");
    return roleRepository.findAll();
  }

  @Override
  public RBACRole createRole(RBACRole role) {
    log.trace("createRole, role: {}", role);
    return roleRepository.save(role);
  }

  @Override
  public RBACRole updateRole(RBACRole role) {
    log.trace("updateRole, role: {}", role);

    roleRepository
        .findById(role.id())
        .orElseThrow(() -> AccessControlNotFoundException.role(role.id()));

    return roleRepository.save(role);
  }

  @Override
  public void deleteRole(UUID roleId) {
    log.trace("deleteRole, roleId: {}", roleId);
    roleRepository.deleteById(roleId);
  }

  @Override
  public List<RBACRole> getRolesByPrincipalLogin(String login) {
    log.trace("getRolesByPrincipalLogin, login: {}", login);

    return assignementRepository.findByPrincipal(login).stream()
        .map(RBACAssignment::role)
        .distinct()
        .toList();
  }
}
