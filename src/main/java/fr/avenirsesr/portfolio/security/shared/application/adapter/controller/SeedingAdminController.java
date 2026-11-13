package fr.avenirsesr.portfolio.security.shared.application.adapter.controller;

import fr.avenirsesr.portfolio.security.shared.infrastructure.adapter.seeder.SeederOrchestrator;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/security/seeder")
@RequiredArgsConstructor
public class SeedingAdminController {

  private final SeederOrchestrator seederOrchestrator;

  @Value("${admin.token:}")
  private String adminToken;

  @PostMapping("/reset")
  public ResponseEntity<String> run(@RequestHeader(name = "X-ADMIN-TOKEN") String token) {
    if (adminToken == null || adminToken.isBlank()) {
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    if (!MessageDigest.isEqual(
        adminToken.getBytes(StandardCharsets.UTF_8), token.getBytes(StandardCharsets.UTF_8))) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    try {
      log.warn("Resetting and reseeding security database via admin endpoint");
      seederOrchestrator.resetAndSeed();
    } catch (IllegalStateException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    } catch (Exception e) {
      log.error("Security reset and seeding failed", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Security seeding failed: " + e.getMessage());
    }
    return ResponseEntity.ok("Security seeding is completed.");
  }
}
