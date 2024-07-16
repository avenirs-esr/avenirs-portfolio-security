package fr.avenirsesr.portfolio.security.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
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

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = "classpath:test-fixtures.sql")
@Transactional
//@WebMvcTest(AccessControlController.class)
class AccessControlControllerTest {

	

	@Autowired
	private AccessControlService accessControlService;
	
	@Autowired
	private TestRestTemplate restTemplate;

	
	@Test
	void testHasAccess() throws Exception {
		var restClient = RestClient.create();
		var response = restClient
				.get()
				.uri("http://localhost:12000/access-control")
				.retrieve()
.toEntity(String.class);
		System.out.println("===>" + response);
				assertThat(restTemplate.getForObject("/access-control" , String.class)).contains("foo");
//		 mvc.perform(MockMvcRequestBuilders
//		  			.get("/employees")
//		  			.accept(MediaType.APPLICATION_JSON));
	
	}

}
