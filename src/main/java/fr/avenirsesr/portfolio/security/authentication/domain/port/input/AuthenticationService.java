package fr.avenirsesr.portfolio.security.authentication.domain.port.input;

import fr.avenirsesr.portfolio.security.authentication.domain.model.AuthContext;
import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCSession;

public interface AuthenticationService {

  String generateAuthorizationUrl(String host, String redirect, String codeChallenge);

  OIDCSession createSessionFromAuthorizationCode(String host, String code, String codeVerifier);

  AuthContext getAuthenticatedContext(OIDCSession oidcSession);
}
