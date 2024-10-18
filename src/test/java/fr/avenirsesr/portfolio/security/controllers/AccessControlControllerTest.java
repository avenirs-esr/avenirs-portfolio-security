package fr.avenirsesr.portfolio.security.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import jakarta.transaction.Transactional;


/**
 * <h1>AccessControlControllerTest</h1>
 * <p>
 * Description: general test case, bad header, missing token, etc. See specific tests are located in specific test files.
 *
 * <h2>Version:</h2>
 * 1.0
 *
 * <h2>Author:</h2>
 * Arnaud Deman
 *
 * <h2>Since:</h2>
 * 18 Sept 2024
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "classpath:db/test-fixtures-commons.sql")
@Transactional
class AccessControlControllerTest {

    @Value("${avenirs.access.control}")
    private String accessControlEndPoint;

    @Value("${avenirs.access.control.feedback}")
    private String feedbackEndPoint;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testIsAuthorizedWithoutHeader() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("uri", "/ac")
                        .param("method", HttpMethod.GET.name()))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    void testIsAuthorizedWithoutURI() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("x-authorization", "access-token-value")
                        .param("method", HttpMethod.GET.name()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void testIsAuthorizedWithoutMethod() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("x-authorization", "access-token-value")
                        .param("uri", feedbackEndPoint))
                .andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    void testIsAuthorizedNotGranted() throws Exception {

        String token = "invalid token";
        mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("x-authorization", token)
                        .param("uri", feedbackEndPoint)
                        .param("method", HttpMethod.GET.name())
                        .param("resource", "1"))
                .andDo(print()).andExpect(status().isForbidden());


    }


}
