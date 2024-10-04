package fr.avenirsesr.portfolio.security.controllers;

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

import fr.avenirsesr.portfolio.security.AccessTokenHelper;
import jakarta.transaction.Transactional;



/**
 *
 * <h1>AccessControlControllerCase1Test</h1>
 * <p>
 * Description:  test case 1. User deman is owner of resource  of type portfolio.
 * </p>
 * For more details
 * <a href="https://avenirs-esr.github.io/dev-doc/arch-soft-specif-security-rbac-test-case1/">
 *     	https://avenirs-esr.github.io/dev-doc/arch-soft-specif-security-rbac-test-case1/
 * </a>}
 *
 * <h2>Version:</h2>
 * 1.0.0
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
@Sql(scripts={
		"classpath:db/test-fixtures-commons.sql",
		"classpath:db/test-fixtures-rbac-case1.sql"
})
@Transactional
class AccessControlControllerCase1Test {

	@Value("${avenirs.rbac.case1.user}")
	private String user;
	
	@Value("${avenirs.rbac.case1.password}")
	private String password;
	
	@Value("${avenirs.rbac.case1.granted.resource.id}")
	private String grantedResourceId;
	
	@Value("${avenirs.rbac.case1.not.granted.resource.id}")
	private String notGrantedResourceId;
	
	@Value("${avenirs.rbac.unprivileged.user}")
	private String unprivilegedUser;
	
	@Value("${avenirs.rbac.unprivileged.password}")
	private String unprivilegedPassword;
	
	@Value("${avenirs.access.control}")
	private String accessControlEndPoint;
	
	@Value("${avenirs.access.control.share.read}")
	private String accessControlShareReadEndPoint;
	
	@Value("${avenirs.access.control.share.write}")
	private String accessControlShareWriteEndPoint;
	
	@Value("${avenirs.access.control.display}")
	private String accessControlDisplayEndPoint;
	
	@Value("${avenirs.access.control.edit}")
	private String accessControlEditEndPoint;
	
	@Value("${avenirs.access.control.feedback}")
	private String accessControlFeedbackEndPoint;
	
	@Value("${avenirs.access.control.delete}")
	private String accessControlDeleteEndPoint;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private AccessTokenHelper accessTokenHelper;
	
	@Test
	void testOwnerCanShareReadGrantedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(user, password))
				.param("resourceId", grantedResourceId)
				.param("uri", accessControlShareReadEndPoint)
				.param("method", HttpMethod.POST.name()))//.andDo(print())
				.andExpect(status().is2xxSuccessful());
		
		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(user, password))
				.param("resourceId", grantedResourceId)
				.param("uri", accessControlShareReadEndPoint)
				.param("method", HttpMethod.PUT.name()))//.andDo(print())
				.andExpect(status().is2xxSuccessful());
	}
	
	@Test
	void testOwnerCannotShareReadNotGrantedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(user, password))
				.param("resourceId", notGrantedResourceId)
				.param("uri", accessControlShareReadEndPoint)
				.param("method", HttpMethod.POST.name()))//.andDo(print())
				.andExpect(status().isForbidden());
		
		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(user, password))
				.param("resourceId", notGrantedResourceId)
				.param("uri", accessControlShareReadEndPoint)
				.param("method", HttpMethod.PUT.name()))//.andDo(print())
				.andExpect(status().isForbidden());
	}
	
	@Test
	void testUnprivilegedCannotShareReadGrantedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(unprivilegedUser, unprivilegedPassword))
				.param("uri", accessControlShareReadEndPoint)
				 .param("method", HttpMethod.POST.name()))//.andDo(print())
				.andExpect(status().isForbidden());
		
		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(unprivilegedUser, unprivilegedPassword))
				.param("uri", accessControlShareReadEndPoint)
				 .param("method", HttpMethod.PUT.name()))//.andDo(print())
				.andExpect(status().isForbidden());
	}
	
	@Test
	void testOwnerCanShareWriteGrantedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(user, password))
				.param("resourceId", grantedResourceId)
				.param("uri", accessControlShareWriteEndPoint)
				.param("method", HttpMethod.POST.name()))//.andDo(print())
				.andExpect(status().is2xxSuccessful());

		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(user, password))
				.param("resourceId", grantedResourceId)
				.param("uri", accessControlShareWriteEndPoint)
				.param("method", HttpMethod.PUT.name()))//.andDo(print())
				.andExpect(status().is2xxSuccessful());
	}
	
	@Test
	void testOwnerCannotShareWriteNotGrantedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(user, password))
				.param("resourceId", notGrantedResourceId)
				.param("uri", accessControlShareWriteEndPoint)
				.param("method", HttpMethod.POST.name()))//.andDo(print())
				.andExpect(status().isForbidden());
		
		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(user, password))
				.param("resourceId", notGrantedResourceId)
				.param("uri", accessControlShareWriteEndPoint)
				.param("method", HttpMethod.PUT.name()))//.andDo(print())
				.andExpect(status().isForbidden());
	}
	
	@Test
	void testUnprivilegedCannotShareWriteGrantedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(unprivilegedUser, unprivilegedPassword))
				.param("resourceId", grantedResourceId)
				.param("uri", accessControlShareWriteEndPoint)
				.param("method", HttpMethod.POST.name()))//.andDo(print())
				.andExpect(status().isForbidden());
		
		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(unprivilegedUser, unprivilegedPassword))
				.param("resourceId", grantedResourceId)
				.param("uri", accessControlShareWriteEndPoint)
				.param("method", HttpMethod.PUT.name()))//.andDo(print())
				.andExpect(status().isForbidden());
				
	}
	
	@Test
	void testOwnerCanDisplayGrantedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(user, password))
				.param("resourceId", grantedResourceId)
				.param("uri", accessControlDisplayEndPoint)
				.param("method", HttpMethod.GET.name()))//.andDo(print())
				.andExpect(status().is2xxSuccessful());
	}
	
	@Test
	void testOwnerCannotDisplayNotGrantedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(user, password))
				.param("resourceId", notGrantedResourceId)
				.param("uri", accessControlDisplayEndPoint)
				.param("method", HttpMethod.GET.name()))//.andDo(print())
				.andExpect(status().isForbidden());
	}
	
	@Test
	void testUnprivilegedCannotDisplayGrantedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(unprivilegedUser, unprivilegedPassword))
				.param("resourceId", grantedResourceId)
				.param("uri", accessControlDisplayEndPoint)
				.param("method", HttpMethod.GET.name()))//.andDo(print())
				.andExpect(status().isForbidden());
	}
		
	@Test
	void testOwnerCanEditGrantedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(user, password))
				.param("resourceId", grantedResourceId)
				.param("uri", accessControlEditEndPoint)
				.param("method", HttpMethod.POST.name()))//.andDo(print())
				.andExpect(status().is2xxSuccessful());
		
		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(user, password))
				.param("resourceId", grantedResourceId)
				.param("uri", accessControlEditEndPoint)
				.param("method", HttpMethod.PUT.name()))//.andDo(print())
				.andExpect(status().is2xxSuccessful());
	}
	
	@Test
	void testOwnerCannotEditNotGrantedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(user, password))
				.param("resourceId", notGrantedResourceId)
				.param("uri", accessControlEditEndPoint)
				.param("method", HttpMethod.POST.name()))//.andDo(print())
				.andExpect(status().isForbidden());
		
		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(user, password))
				.param("resourceId", notGrantedResourceId)
				.param("uri", accessControlEditEndPoint)
				.param("method", HttpMethod.PUT.name()))//.andDo(print())
				.andExpect(status().isForbidden());
	}
	
	@Test
	void testUnprivilegedCannotEditGrantedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(unprivilegedUser, unprivilegedPassword))
				.param("resourceId", grantedResourceId)
				.param("uri", accessControlEditEndPoint)
				.param("method", HttpMethod.POST.name()))//.andDo(print())
				.andExpect(status().isForbidden());
		
		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(unprivilegedUser, unprivilegedPassword))
				.param("resourceId", grantedResourceId)
				.param("uri", accessControlEditEndPoint)
				.param("method", HttpMethod.PUT.name()))//.andDo(print())
				.andExpect(status().isForbidden());
				
	}
	
	
	@Test
	void testOwnerCanFeedbackGrantedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(user, password))
				.param("resourceId", grantedResourceId)
				.param("uri", accessControlFeedbackEndPoint)
				.param("method", HttpMethod.POST.name()))//.andDo(print())
				.andExpect(status().is2xxSuccessful());
		
		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(user, password))
				.param("resourceId", grantedResourceId)
				.param("uri", accessControlFeedbackEndPoint)
				.param("method", HttpMethod.PUT.name()))//.andDo(print())
				.andExpect(status().is2xxSuccessful());
	}
	
	@Test
	void testOwnerCannotFeedbackNotGrantedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(user, password))
				.param("resourceId", notGrantedResourceId)
				.param("uri", accessControlFeedbackEndPoint)
				.param("method", HttpMethod.POST.name()))//.andDo(print())
				.andExpect(status().isForbidden());
		
		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(user, password))
				.param("resourceId", notGrantedResourceId)
				.param("uri", accessControlFeedbackEndPoint)
				.param("method", HttpMethod.PUT.name()))//.andDo(print())
				.andExpect(status().isForbidden());
	}
	
	@Test
	void testUnprivilegedCannotFeedbackGrantedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(unprivilegedUser, unprivilegedPassword))
				.param("resourceId", grantedResourceId)
				.param("uri", accessControlFeedbackEndPoint)
				.param("method", HttpMethod.POST.name()))//.andDo(print())
				.andExpect(status().isForbidden());
		
		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(unprivilegedUser, unprivilegedPassword))
				.param("resourceId", grantedResourceId)
				.param("uri", accessControlFeedbackEndPoint)
				.param("method", HttpMethod.PUT.name()))//.andDo(print())
				.andExpect(status().isForbidden());
				
	}

	@Test
	void testOwnerCanDeleteGrantedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.header("x-authorization", accessTokenHelper.provideAccessToken(user, password))
						.param("resourceId", grantedResourceId)
						.param("uri", accessControlDeleteEndPoint)
						.param("method", HttpMethod.DELETE.name()))
				.andExpect(status().is2xxSuccessful());
	}

	@Test
	void testOwnerCannotDeleteNotGrantedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.header("x-authorization", accessTokenHelper.provideAccessToken(user, password))
						.param("resourceId", notGrantedResourceId)
						.param("uri", accessControlDeleteEndPoint)
						.param("method", HttpMethod.DELETE.name()))
				.andExpect(status().isForbidden());

	}

	@Test
	void testUnprivilegedCannotDeleteGrantedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.header("x-authorization", accessTokenHelper.provideAccessToken(unprivilegedUser, unprivilegedPassword))
						.param("resourceId", grantedResourceId)
						.param("uri", accessControlDeleteEndPoint)
						.param("method", HttpMethod.DELETE.name()))//.andDo(print())
				.andExpect(status().isForbidden());

	}
}
