package fr.avenirsesr.portfolio.security.authentication.application.adapter.dto;

import java.util.UUID;

public record AuthContextDTO(boolean authenticated, UUID userId, String login) {}
