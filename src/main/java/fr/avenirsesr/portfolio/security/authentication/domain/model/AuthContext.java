package fr.avenirsesr.portfolio.security.authentication.domain.model;

import java.util.Set;

public record AuthContext(boolean authenticated, String login, Set<String> authorities) {}
