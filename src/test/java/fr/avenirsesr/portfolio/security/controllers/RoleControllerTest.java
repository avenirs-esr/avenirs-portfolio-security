package fr.avenirsesr.portfolio.security.controllers;

import fr.avenirsesr.portfolio.security.AccessTokenHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RoleControllerTest {

    @Value("${avenirs.test.role.controller.user.login}")
    private String userLogin;

    @Value("${avenirs.test.role.controller.user.password}")
    private String userPassword;

    @Value("${avenirs.test.role.controller.expected.roles}")
    private String[] expectedRoles;


    @Autowired
    AccessTokenHelper accessTokenHelper;

    @Autowired
    RoleController roleController;


    @Test
    void getRolesWithValidTokenWithoutFixtures() {
        try {
            String token = accessTokenHelper.provideAccessToken(userLogin, userPassword);
            List<String> roles = roleController.getRoles(token);
            assertTrue(roles.isEmpty(), "Valid token, No role");
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Sql(scripts={
            "classpath:db/test-fixtures-commons.sql",
            "classpath:db/test-fixtures-role-controller.sql"
    })
    @Transactional
    @Test
    void getRolesWithValidTokenWithFixtures() {
        try {
            String token = accessTokenHelper.provideAccessToken(userLogin, userPassword);
            List<String> roles = roleController.getRoles(token);
            assertEquals(expectedRoles.length, roles.size(), "Roles number");
            assertTrue(roles.containsAll(Arrays.asList(expectedRoles)), "Role list");

        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    void getRolesWithInvalidToken() {

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () ->roleController.getRoles("invalid-token"),
                "Invalid token throws exception"
        );
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }
}