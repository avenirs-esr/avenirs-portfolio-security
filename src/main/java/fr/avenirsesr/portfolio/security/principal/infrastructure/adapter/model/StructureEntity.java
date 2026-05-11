/** */
package fr.avenirsesr.portfolio.security.principal.infrastructure.adapter.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 *
 *
 * <h1>Structure</h1>
 *
 * <p>Description: is used to represent the notion of structure.
 *
 * <h2>Version:</h2>
 *
 * 1.0
 *
 * <h2>Author:</h2>
 *
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 *
 * 1 Oct 2024
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "structure",
    indexes = {@Index(name = "structure_name_idx", columnList = "name")})
public class StructureEntity {

  /** Database Id. */
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  /** Name of the structure. */
  @Column(length = 255, nullable = false)
  private String name;

  /** The description of the structure. */
  @Column(length = 255, nullable = false)
  private String description;
}
