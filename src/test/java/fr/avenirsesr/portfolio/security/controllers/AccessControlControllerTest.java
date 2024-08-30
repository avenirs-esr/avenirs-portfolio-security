package fr.avenirsesr.portfolio.security.controllers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.avenirsesr.portfolio.security.models.AccessControlResponse;
import fr.avenirsesr.portfolio.security.services.RBACAssignmentService;
import jakarta.transaction.Transactional;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "classpath:test-fixtures.sql")
@Transactional
class AccessControlControllerTest {
	@Autowired
	RBACAssignmentService srv;
	
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void testHasAccessWithoutHeader() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get("/access-control").contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).param("uri", "/ac").param("method", "get")).andDo(print())
				.andExpect(status().isBadRequest());

	}

	@Test
	void testHasAccessWithoutURI() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get("/access-control").contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).header("x-authorization", "accesstokenvalue")
				.param("method", "get")).andDo(print()).andExpect(status().isBadRequest());
	}

	@Test
	void testHasAccessWithoutMethod() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get("/access-control").contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).header("x-authorization", "accesstokenvalue").param("uri", "/feedback"))
				.andDo(print()).andExpect(status().isBadRequest());
	}

	@Test
	void testHasAccessNotGranted() throws Exception {
		
		String token = "invalid token";
		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/access-control")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", token)
				.param("uri", "/feedback")
				.param("method", "get")
				.param("resource", "1"))
				.andDo(print()).andExpect(status().isOk());

		MvcResult result = resultActions.andReturn();

		AccessControlResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), AccessControlResponse.class);
		assertFalse(response.isGranted());
	}

}
