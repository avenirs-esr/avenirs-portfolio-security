package fr.avenirsesr.portfolio.security.authentication.application.adapter.dto;

import java.util.UUID;

public record IntrospectResponseDTO(boolean active, String uniqueSecurityName, UUID userId) {}
