package fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.dto;

import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class AccessControlGrantResponseDTO {

  private String login;
  private boolean granted;
  private UUID assignmentId;
  private String error;
}
