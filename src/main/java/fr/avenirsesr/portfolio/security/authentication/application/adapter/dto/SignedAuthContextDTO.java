package fr.avenirsesr.portfolio.security.authentication.application.adapter.dto;

public record SignedAuthContextDTO(String payload, String signature, String kid) {}
