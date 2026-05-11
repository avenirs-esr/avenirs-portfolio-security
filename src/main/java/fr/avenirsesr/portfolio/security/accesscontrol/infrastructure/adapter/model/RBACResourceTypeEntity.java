package fr.avenirsesr.portfolio.security.accesscontrol.infrastructure.adapter.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Data;
import lombok.experimental.Accessors;

/** Resources type. The type is used to determine if a resource is a portfolio, a SAE , etc. */
@Data
@Accessors(chain = true)
@Entity
@Table(name = "resource_type")
public class RBACResourceTypeEntity {

  /** Database id. */
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  /** Name of the resource type. */
  @Column(length = 255, nullable = false, unique = true)
  private String name;

  /** Description of the resource type. */
  @Column(length = 255, nullable = false)
  private String description;
}
