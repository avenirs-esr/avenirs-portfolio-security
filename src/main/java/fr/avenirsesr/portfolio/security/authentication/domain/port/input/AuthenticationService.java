package fr.avenirsesr.portfolio.security.authentication.domain.port.input;

import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCSession;
import fr.avenirsesr.portfolio.security.authentication.domain.model.SignedAuthContext;

public interface AuthenticationService {

  String generateAuthorizationUrl(String host, String redirect, String codeChallenge);

  OIDCSession createSessionFromAuthorizationCode(String host, String code, String codeVerifier);

  SignedAuthContext getSignedAuthenticatedContext(OIDCSession oidcSession);

  OIDCSession refreshSessionIfNeeded(OIDCSession oidcSession);
}
