package fr.avenirsesr.portfolio.security.accesscontrol.application.adapter.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.security.authentication.infrastructure.adapter.service.PrincipalGrantedAuthoritiesService;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class InternalAccessControlControllerIT {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private PrincipalGrantedAuthoritiesService authorizationsPort;

  @Test
  void shouldReturnSortedRolesOfLogin() throws Exception {
    BddLogger.given("a login holding two roles");
    when(authorizationsPort.resolveRoles("jdoe@univ.fr"))
        .thenReturn(Set.of("ROLE_STAFF", "ROLE_ADMIN"));

    BddLogger.when("getting the roles of the login");
    var result =
        mockMvc.perform(get("/internal/access-control/roles").param("login", "jdoe@univ.fr"));

    BddLogger.then("it should return them sorted");
    result
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"))
        .andExpect(jsonPath("$.roles[1]").value("ROLE_STAFF"));
  }

  @Test
  void shouldReturnNoRole_whenLoginIsUnknown() throws Exception {
    BddLogger.given("a login without any assignment");
    when(authorizationsPort.resolveRoles("nobody")).thenReturn(Set.of());

    BddLogger.when("getting the roles of the login");
    var result = mockMvc.perform(get("/internal/access-control/roles").param("login", "nobody"));

    BddLogger.then("it should return an empty list");
    result.andExpect(status().isOk()).andExpect(jsonPath("$.roles").isEmpty());
  }
}
