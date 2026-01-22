package fr.avenirsesr.portfolio.security.controller;

import fr.avenirsesr.portfolio.security.delegate.SecurityDelegate;
import fr.avenirsesr.portfolio.security.model.RBACAssignment;
import fr.avenirsesr.portfolio.security.model.RBACRole;
import fr.avenirsesr.portfolio.security.repository.RBACAssignmentSpecificationHelper;
import fr.avenirsesr.portfolio.security.service.RBACAssignmentService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 *
 * <h1>RoleController</h1>
 *
 * <p><b>Description:</b> used to retrieve the roles associated to a principal.
 *
 * <h2>Version:</h2>
 *
 * 1.0.0
 *
 * <h2>Author:</h2>
 *
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 *
 * 28/10/2024
 */
@Slf4j
@RestController
public class RoleController {

  /** Assignment service, used to retrieve roles assigned to a principal for instance. */
  @Autowired private RBACAssignmentService assignmentService;

  /** Delegate for the security checks. */
  @Autowired private SecurityDelegate securityDelegate;

  /**
   * Gives the roles associated to a users.
   *
   * @return The list of role names.
   */
  @SuppressWarnings("SpringOmittedPathVariableParameterInspection")
  @GetMapping("${avenirs.access.control.roles}")
  public List<RBACRole> getRoles() {

    String login = securityDelegate.getAuthenticatedUserLogin();

    log.trace("getRoles, login: {}", login);

    List<RBACAssignment> assignments =
        assignmentService.getAllAssignmentsBySpecification(
            RBACAssignmentSpecificationHelper.filterByPrincipal(login));

    List<RBACRole> roles = assignments.stream().map(RBACAssignment::getRole).toList();
    log.trace("Role for {}: {}", login, roles);
    return roles;
  }
}
