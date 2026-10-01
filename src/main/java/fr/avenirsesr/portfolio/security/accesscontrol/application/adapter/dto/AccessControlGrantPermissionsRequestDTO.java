package fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.dto;

import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class AccessControlGrantPermissionsRequestDTO {
  private String login;
  private List<String> permissions;
  private List<String> permissionGroups;
  private List<UUID> resourceIds;
  private String validityStart;
  private String validityEnd;
  private List<UUID> structureIds;
}
