package fr.avenirsesr.portfolio.security.authentication.domain.port.output;

import fr.avenirsesr.portfolio.security.authentication.domain.model.OIDCAccessToken;
import java.util.Map;
import java.util.Optional;

public interface JWTServicePort {
  Optional<Map<String, Object>> parseAndCheckSignature(OIDCAccessToken accessToken);
}
