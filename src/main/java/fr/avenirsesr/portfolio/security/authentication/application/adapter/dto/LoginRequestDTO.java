package fr.avenirsesr.portfolio.security.authentication.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LoginRequestDTO {

  private String login;

  @ToString.Exclude private String password;

  @ToString.Include(name = "password")
  @Schema(hidden = true)
  public String getMaskedPassword() {
    return password != null ? "****" : null;
  }
}
