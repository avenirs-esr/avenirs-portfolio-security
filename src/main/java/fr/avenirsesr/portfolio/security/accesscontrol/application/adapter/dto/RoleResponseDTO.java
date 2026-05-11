package fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.dto;

import java.util.UUID;

public record RoleResponseDTO(UUID id, String name, String description) {}
