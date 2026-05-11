package fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.controller;

import fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.dto.RoleResponseDTO;
import fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.mapper.RoleDTOMapper;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.input.RBACRoleService;
import fr.avenirsesr.portfolio.security.shared.infrastructure.adapter.delegate.SecurityDelegate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RoleController {

  private final RBACRoleService roleService;
  private final SecurityDelegate securityDelegate;

  @GetMapping("${avenirs.access.control.roles}")
  public List<RoleResponseDTO> getRoles() {
    String login = securityDelegate.getAuthenticatedUserLogin();

    log.trace("getRoles, login: {}", login);

    List<RoleResponseDTO> roles =
        roleService.getRolesByPrincipalLogin(login).stream()
            .map(RoleDTOMapper::fromDomain)
            .toList();

    log.trace("Roles for {}: {}", login, roles);

    return roles;
  }
}
