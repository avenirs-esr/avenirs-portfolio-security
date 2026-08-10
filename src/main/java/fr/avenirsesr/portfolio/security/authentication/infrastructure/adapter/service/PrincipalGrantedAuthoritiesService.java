package fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACAssignment;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACContext;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACPermission;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACAssignmentRepository;
import fr.avenirsesr.portfolio.security.authentication.domain.port.output.AuthorizationsPort;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PrincipalGrantedAuthoritiesService implements AuthorizationsPort {

  private final RBACAssignmentRepository assignmentRepository;

  public Set<GrantedAuthority> loadAuthorities(String login) {
    Set<GrantedAuthority> authorities =
        resolveAuthorities(login).stream()
            .map((String authority) -> (GrantedAuthority) new SimpleGrantedAuthority(authority))
            .collect(Collectors.toUnmodifiableSet());

    log.trace("loadAuthorities, login: {}, authorities: {}", login, authorities);

    return authorities;
  }

  @Override
  public Set<String> resolveAuthorities(String login) {
    LocalDateTime now = LocalDateTime.now();

    return assignmentRepository.findByPrincipal(login).stream()
        .filter(assignment -> isCurrentlyValid(assignment, now))
        .flatMap(assignment -> assignment.role().permissions().stream())
        .map(RBACPermission::name)
        .collect(Collectors.toUnmodifiableSet());
  }

  @Override
  public Set<String> resolveRoles(String login) {
    LocalDateTime now = LocalDateTime.now();

    return assignmentRepository.findByPrincipal(login).stream()
        .filter(assignment -> isCurrentlyValid(assignment, now))
        .map(assignment -> assignment.role().name())
        .collect(Collectors.toUnmodifiableSet());
  }

  private boolean isCurrentlyValid(RBACAssignment assignment, LocalDateTime now) {
    RBACContext context = assignment.context();

    if (context == null) {
      return true;
    }

    boolean started = context.validityStart() == null || !context.validityStart().isAfter(now);
    boolean notEnded = context.validityEnd() == null || !context.validityEnd().isBefore(now);

    return started && notEnded;
  }
}
