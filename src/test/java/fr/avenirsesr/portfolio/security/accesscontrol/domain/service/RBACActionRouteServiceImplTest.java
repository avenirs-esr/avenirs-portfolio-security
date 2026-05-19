package fr.avenirsesr.portfolio.security.accesscontrol.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACAction;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.model.RBACActionRoute;
import fr.avenirsesr.portfolio.security.accesscontrol.domain.port.output.repository.RBACActionRouteRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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

  @Nested
  class GivenARBACActionRouteService {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("a RBAC action route service");
    }

    @Nested
    class WhenFindingByUriAndMethod {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("finding an action route by URI and method");
      }

      @Nested
      class AndTheActionRouteExists {
        private Optional<RBACActionRoute> result;
        private RBACActionRoute expectedRoute;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the action route exists");

          RBACAction action = new RBACAction(ACTION_ID, ACTION_NAME, "Do feedback", List.of());
          expectedRoute = new RBACActionRoute(ACTION_ROUTE_ID, URI, METHOD, action);

          when(actionRouteRepository.findByUriAndMethod(URI, METHOD))
              .thenReturn(Optional.of(expectedRoute));

          result = service.findByUriAndMethod(URI, METHOD);
        }

        @Test
        void thenItShouldReturnTheActionRoute() {
          BddLogger.then("it should return the action route");

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
      }

      @Nested
      class AndTheActionRouteDoesNotExist {
        private Optional<RBACActionRoute> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the action route does not exist");

          when(actionRouteRepository.findByUriAndMethod(URI, METHOD)).thenReturn(Optional.empty());

          result = service.findByUriAndMethod(URI, METHOD);
        }

        @Test
        void thenItShouldReturnEmpty() {
          BddLogger.then("it should return empty");

          assertTrue(result.isEmpty());

          verify(actionRouteRepository).findByUriAndMethod(URI, METHOD);
          verifyNoMoreInteractions(actionRouteRepository);
        }
      }
    }
  }
}
