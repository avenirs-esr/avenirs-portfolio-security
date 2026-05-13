package fr.avenirsesr.portfolio.security.authentication.domain.port.input;

public interface AuthenticationService {

  String generateAuthorizationUrl(String host, String redirect);
}
