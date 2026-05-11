package fr.avenirsesr.portfolio.security.accesscontrol.domain.model;

import fr.avenirsesr.portfolio.security.principal.domain.model.Structure;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record RBACContext(
    UUID id, LocalDateTime validityStart, LocalDateTime validityEnd, Set<Structure> structures) {}
