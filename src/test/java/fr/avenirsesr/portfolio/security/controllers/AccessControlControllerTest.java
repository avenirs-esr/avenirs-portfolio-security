package fr.avenirsesr.portfolio.security.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.avenirsesr.portfolio.security.services.AccessControlService;
import jakarta.transaction.Transactional;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "classpath:test-fixtures.sql")
@Transactional
// @WebMvcTest(AccessControlController.class)
class AccessControlControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AccessControlService accessControlService;

	@Autowired
	private TestRestTemplate restTemplate;
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void testHasAccessWithoutHeader() throws Exception {

		mockMvc.perform(
					MockMvcRequestBuilders.get("/access-control")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.param("uri", "/ac")
						.param("method", "get")
					)
					.andDo(print())
					.andExpect(status().isBadRequest());

	}

	@Test
	void testHasAccessWithoutURI() throws Exception {

		mockMvc.perform(
				MockMvcRequestBuilders.get("/access-control")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.header("x-authorization", "accesstokenvalue")
					.param("method", "get")
				)
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	void testHasAccessWithoutMethod() throws Exception {

		mockMvc.perform(
					MockMvcRequestBuilders.get("/access-control")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.header("x-authorization", "accesstokenvalue")
						.param("uri", "/foo")
					)
					.andDo(print())
					.andExpect(status().isBadRequest());
	}

	@Test
	void testHasAccessNotGranted() throws Exception {

		ResultActions resultActions = mockMvc.perform(
				MockMvcRequestBuilders.get("/access-control")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.header("x-authorization", "accesstokenvalue")
					.param("uri", "/ac")
					.param("method", "get")
				)
				.andDo(print())
				.andExpect(status().isOk());

		MvcResult result = resultActions.andReturn();

		boolean granted = objectMapper.readValue(result.getResponse().getContentAsString(), Boolean.class);
		assertFalse(granted);
	}

}
