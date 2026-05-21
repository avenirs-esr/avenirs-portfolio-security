package fr.avenirsesr.portfolio.security.authentication.domain.port.output;

import fr.avenirsesr.portfolio.security.authentication.domain.model.AuthContext;
import fr.avenirsesr.portfolio.security.authentication.domain.model.SignedAuthContext;

public interface AuthContextSigningPort {
  SignedAuthContext sign(AuthContext authContext);
}
