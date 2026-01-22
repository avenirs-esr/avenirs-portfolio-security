package fr.avenirsesr.portfolio.security.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/** Represents the association between an HTTP route (URI + HTTP method) and an RBAC Action. */
@Data
@Accessors(chain = true)
@Entity
@Table(
    name = "action_route",
    uniqueConstraints =
        @UniqueConstraint(
            name = "action_route_uri_method_unique_cstr",
            columnNames = {"uri", "method"}))
public class RBACActionRoute {

  /** Database Id. */
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  /** URI associated to the route. */
  @Column(length = 255, nullable = false)
  private String uri;

  /** method associated to the route. */
  @Column(columnDefinition = "CITEXT", nullable = false)
  private String method;

  /** Action associated to the route. */
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "id_action", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private RBACAction action;
}
