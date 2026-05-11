package fr.avenirsesr.portfolio.security.accesscontrol.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACAction;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACActionRoute;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACActionRouteRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;

@ExtendWith(MockitoExtension.class)
class RBACActionRouteServiceImplTest {

  private static final UUID ACTION_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final UUID ACTION_ROUTE_ID =
      UUID.fromString("00000000-0000-0000-0000-000000000002");

  private static final String URI = "/feedback";
  private static final String METHOD = "POST";
  private static final String ACTION_NAME = "ACT_DO_FEEDBACK";

  @Mock private RBACActionRouteRepository actionRouteRepository;

  private RBACActionRouteServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new RBACActionRouteServiceImpl(actionRouteRepository);
  }

  @Test
  void findByUriAndMethodReturnsActionRouteWhenFound() {
    RBACAction action = new RBACAction(ACTION_ID, ACTION_NAME, "Do feedback", List.of());

    RBACActionRoute expectedRoute = new RBACActionRoute(ACTION_ROUTE_ID, URI, METHOD, action);

    when(actionRouteRepository.findByUriAndMethod(URI, METHOD))
        .thenReturn(Optional.of(expectedRoute));

    Optional<RBACActionRoute> result = service.findByUriAndMethod(URI, METHOD);

    assertTrue(result.isPresent());

    RBACActionRoute actionRoute = result.orElseThrow();

    assertEquals(ACTION_ROUTE_ID, actionRoute.id());
    assertEquals(URI, actionRoute.uri());
    assertTrue(HttpMethod.POST.name().equalsIgnoreCase(actionRoute.method()));
    assertNotNull(actionRoute.action());
    assertEquals(ACTION_ID, actionRoute.action().id());
    assertEquals(ACTION_NAME, actionRoute.action().name());

    verify(actionRouteRepository).findByUriAndMethod(URI, METHOD);
    verifyNoMoreInteractions(actionRouteRepository);
  }

  @Test
  void findByUriAndMethodReturnsEmptyWhenNotFound() {
    when(actionRouteRepository.findByUriAndMethod(URI, METHOD)).thenReturn(Optional.empty());

    Optional<RBACActionRoute> result = service.findByUriAndMethod(URI, METHOD);

    assertTrue(result.isEmpty());

    verify(actionRouteRepository).findByUriAndMethod(URI, METHOD);
    verifyNoMoreInteractions(actionRouteRepository);
  }
}
