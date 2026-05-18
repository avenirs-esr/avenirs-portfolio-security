package fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCSession;
import fr.avenirsesr.portfolio.security.shared.infrastructure.adapter.session.SessionAttributes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthenticationSessionReader {

  public Optional<OIDCSession> readOidcSession(HttpServletRequest request) {
    HttpSession session = request.getSession(false);

    if (session == null) {
      return Optional.empty();
    }

    Object value = session.getAttribute(SessionAttributes.OIDC_SESSION);

    if (!(value instanceof OIDCSession oidcSession)) {
      return Optional.empty();
    }

    return Optional.of(oidcSession);
  }
}
