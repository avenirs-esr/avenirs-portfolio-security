package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/** Permission. A permission is mainly a label and can be required to perform actions. */
@Data
@Accessors(chain = true)
@Entity
@Table(name = "permission")
@NoArgsConstructor
@AllArgsConstructor
public class RBACPermissionEntity {

  /** Database id. */
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  /** Name of the permission. */
  @Column(length = 80, nullable = false, unique = true)
  private String name;

  /** Description of the permission. */
  @Column(length = 255, nullable = false)
  private String description;
}
