package fr.avenirsesr.portfolio.security.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestClient;

import fr.avenirsesr.portfolio.security.services.AccessControlService;
import jakarta.transaction.Transactional;


@ActiveProfiles("test")
@Sql(scripts = "classpath:test-fixtures.sql")
@Transactional
@WebMvcTest(AccessControlController.class)
class AccessControlControllerTest {

	@Autowired
	  private MockMvc mvc;

	@MockBean
	private AccessControlService accessControlService;

	
	@Test
	void testHasAccess() throws Exception {
		 mvc.perform(MockMvcRequestBuilders
		  			.get("/employees")
		  			.accept(MediaType.APPLICATION_JSON));
	
	}

}