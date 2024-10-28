package fr.avenirsesr.portfolio.security.controller;

import fr.avenirsesr.portfolio.security.AccessTokenHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
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


    @BeforeEach
    void setUp() throws Exception {
        String token = accessTokenHelper.provideAccessToken(userLogin, userPassword);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userLogin, token, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getRolesWithValidTokenWithoutFixtures() {
        try {
            List<String> roles = roleController.getRoles();
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
            List<String> roles = roleController.getRoles();
            assertEquals(expectedRoles.length, roles.size(), "Roles number");
            assertTrue(roles.containsAll(Arrays.asList(expectedRoles)), "Role list");

        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    void getRolesWithoutAuthentication() {

        SecurityContextHolder.clearContext();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () ->roleController.getRoles(),
                "Invalid token throws exception"
        );
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }
}