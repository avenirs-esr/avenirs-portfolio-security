package fr.avenirsesr.portfolio.security.shared.application.adapter.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.security.shared.infrastructure.adapter.seeder.SeederOrchestrator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "admin.token=" + SeedingAdminControllerIT.ADMIN_TOKEN)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SeedingAdminControllerIT {

  static final String ADMIN_TOKEN = "test-admin-token";
  private static final String RESET_URL = "/security/seeder/reset";
  private static final String SYNC_URL = "/security/seeder/sync";
  private static final String HEADER = "X-ADMIN-TOKEN";

  @Autowired private MockMvc mockMvc;
  @Autowired private SeedingAdminController controller;

  @MockitoBean private SeederOrchestrator seederOrchestrator;

  @AfterEach
  void restoreToken() {
    ReflectionTestUtils.setField(controller, "adminToken", ADMIN_TOKEN);
  }

  @Nested
  class GivenAResetRequest {

    @Test
    void thenShouldResetAndSeed_whenTokenIsValid() throws Exception {
      BddLogger.given("a valid admin token");

      BddLogger.when("running the security reset and seeding");
      var result = mockMvc.perform(post(RESET_URL).header(HEADER, ADMIN_TOKEN));

      BddLogger.then("it should reset and seed the database");
      result.andExpect(status().isOk());
      verify(seederOrchestrator).resetAndSeed();
    }

    @Test
    void thenShouldReturnForbidden_whenTokenIsInvalid() throws Exception {
      BddLogger.given("an invalid admin token");

      BddLogger.when("running the security reset and seeding");
      var result = mockMvc.perform(post(RESET_URL).header(HEADER, "wrong-token"));

      BddLogger.then("it should be rejected without touching the database");
      result.andExpect(status().isForbidden());
      verify(seederOrchestrator, never()).resetAndSeed();
    }

    @Test
    void thenShouldReturnForbidden_whenTokenHeaderIsMissing() throws Exception {
      BddLogger.given("no admin token header");

      BddLogger.when("running the security reset and seeding");
      var result = mockMvc.perform(post(RESET_URL));

      BddLogger.then("it should be rejected without touching the database");
      result.andExpect(status().isForbidden());
      verify(seederOrchestrator, never()).resetAndSeed();
    }

    @Test
    void thenShouldReturnForbidden_whenAdminTokenIsNotConfigured() throws Exception {
      BddLogger.given("no admin token configured on the server");
      ReflectionTestUtils.setField(controller, "adminToken", "");

      BddLogger.when("running the security reset and seeding");
      var result = mockMvc.perform(post(RESET_URL).header(HEADER, ADMIN_TOKEN));

      BddLogger.then(
          "it should be rejected like any other forbidden call, without touching the database");
      result.andExpect(status().isForbidden());
      verify(seederOrchestrator, never()).resetAndSeed();
    }

    @Test
    void thenShouldReturnConflict_whenSeedingIsAlreadyRunning() throws Exception {
      BddLogger.given("a seeding already running");
      doThrow(new IllegalStateException("Security seeding already running"))
          .when(seederOrchestrator)
          .resetAndSeed();

      BddLogger.when("running the security reset and seeding");
      var result = mockMvc.perform(post(RESET_URL).header(HEADER, ADMIN_TOKEN));

      BddLogger.then("it should return a conflict");
      result
          .andExpect(status().isConflict())
          .andExpect(content().string("Security seeding already running"));
    }

    @Test
    void thenShouldReturnInternalServerError_whenSeedingFails() throws Exception {
      BddLogger.given("a seeding that fails");
      doThrow(new RuntimeException("boom")).when(seederOrchestrator).resetAndSeed();

      BddLogger.when("running the security reset and seeding");
      var result = mockMvc.perform(post(RESET_URL).header(HEADER, ADMIN_TOKEN));

      BddLogger.then("it should return an internal server error");
      result.andExpect(status().isInternalServerError());
    }
  }

  @Nested
  class GivenASyncRequest {

    @Test
    void thenShouldSeedMissingOnly_whenTokenIsValid() throws Exception {
      BddLogger.given("a valid admin token");

      BddLogger.when("synchronizing the security seeding");
      var result = mockMvc.perform(post(SYNC_URL).header(HEADER, ADMIN_TOKEN));

      BddLogger.then("it should seed the missing data without resetting the database");
      result.andExpect(status().isOk());
      verify(seederOrchestrator).seedMissing();
      verify(seederOrchestrator, never()).resetAndSeed();
    }

    @Test
    void thenShouldReturnForbidden_whenTokenIsInvalid() throws Exception {
      BddLogger.given("an invalid admin token");

      BddLogger.when("synchronizing the security seeding");
      var result = mockMvc.perform(post(SYNC_URL).header(HEADER, "wrong-token"));

      BddLogger.then("it should be rejected without touching the database");
      result.andExpect(status().isForbidden());
      verify(seederOrchestrator, never()).seedMissing();
    }

    @Test
    void thenShouldReturnConflict_whenSyncIsAlreadyRunning() throws Exception {
      BddLogger.given("a seeding already running");
      doThrow(new IllegalStateException("Security seeding already running"))
          .when(seederOrchestrator)
          .seedMissing();

      BddLogger.when("synchronizing the security seeding");
      var result = mockMvc.perform(post(SYNC_URL).header(HEADER, ADMIN_TOKEN));

      BddLogger.then("it should return a conflict");
      result
          .andExpect(status().isConflict())
          .andExpect(content().string("Security seeding already running"));
    }
  }
}
