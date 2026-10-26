package fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.controller;

import fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.dto.UserRolesDTO;
import fr.avenirsesr.portfolio.security.authentication.domain.port.output.AuthorizationsPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/access-control")
public class InternalAccessControlController {

  private final AuthorizationsPort authorizationsPort;

  @GetMapping("/roles")
  public UserRolesDTO getRolesOfLogin(@RequestParam String login) {
    log.trace("getRolesOfLogin, login: {}", login);

    return new UserRolesDTO(authorizationsPort.resolveRoles(login).stream().sorted().toList());
  }
}
