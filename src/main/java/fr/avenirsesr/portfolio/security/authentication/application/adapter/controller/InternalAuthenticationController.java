package fr.avenirsesr.portfolio.security.authentication.application.adapter.controller;

import fr.avenirsesr.portfolio.security.authentication.application.adapter.dto.AuthContextDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/auth")
public class InternalAuthenticationController {
  @GetMapping("/context")
  public AuthContextDTO context() {
    return null;
  }
}
