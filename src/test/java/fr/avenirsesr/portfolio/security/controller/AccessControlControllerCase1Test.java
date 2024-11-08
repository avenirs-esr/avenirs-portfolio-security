package fr.avenirsesr.portfolio.security.controller;

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
 * </a>
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

	@Value("${avenirs.test.rbac.case1.user.login}")
	private String userLogin;
	
	@Value("${avenirs.test.rbac.case1.user.password}")
	private String userPassword;
	
	@Value("${avenirs.test.rbac.case1.authorized.resource.id}")
	private String authorizedResourceId;
	
	@Value("${avenirs.test.rbac.case1.unauthorized.resource.id}")
	private String unauthorizedResourceId;
	
	@Value("${avenirs.test.rbac.unprivileged.user.login}")
	private String unprivilegedUserLogin;
	
	@Value("${avenirs.test.rbac.unprivileged.user.password}")
	private String unprivilegedUserPassword;
	
	@Value("${avenirs.access.control.authorize}")
	private String authorizeEndPoint;
	
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
	void testOwnerCanShareReadAuthorizedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(authorizeEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
				.param("resourceId", authorizedResourceId)
				.param("uri", accessControlShareReadEndPoint)
				.param("method", HttpMethod.POST.name()))//.andDo(print())
				.andExpect(status().is2xxSuccessful());
		
		mockMvc.perform(MockMvcRequestBuilders.get(authorizeEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
				.param("resourceId", authorizedResourceId)
				.param("uri", accessControlShareReadEndPoint)
				.param("method", HttpMethod.PUT.name()))//.andDo(print())
				.andExpect(status().is2xxSuccessful());
	}
	
	@Test
	void testOwnerCannotShareReadUnauthorizedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(authorizeEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
				.param("resourceId", unauthorizedResourceId)
				.param("uri", accessControlShareReadEndPoint)
				.param("method", HttpMethod.POST.name()))//.andDo(print())
				.andExpect(status().isForbidden());
		
		mockMvc.perform(MockMvcRequestBuilders.get(authorizeEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
				.param("resourceId", unauthorizedResourceId)
				.param("uri", accessControlShareReadEndPoint)
				.param("method", HttpMethod.PUT.name()))//.andDo(print())
				.andExpect(status().isForbidden());
	}
	
	@Test
	void testUnprivilegedCannotShareReadAuthorizedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(authorizeEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(unprivilegedUserLogin, unprivilegedUserPassword))
				.param("uri", accessControlShareReadEndPoint)
				 .param("method", HttpMethod.POST.name()))//.andDo(print())
				.andExpect(status().isForbidden());
		
		mockMvc.perform(MockMvcRequestBuilders.get(authorizeEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(unprivilegedUserLogin, unprivilegedUserPassword))
				.param("uri", accessControlShareReadEndPoint)
				 .param("method", HttpMethod.PUT.name()))//.andDo(print())
				.andExpect(status().isForbidden());
	}
	
	@Test
	void testOwnerCanShareWriteAuthorizedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(authorizeEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
				.param("resourceId", authorizedResourceId)
				.param("uri", accessControlShareWriteEndPoint)
				.param("method", HttpMethod.POST.name()))//.andDo(print())
				.andExpect(status().is2xxSuccessful());

		mockMvc.perform(MockMvcRequestBuilders.get(authorizeEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
				.param("resourceId", authorizedResourceId)
				.param("uri", accessControlShareWriteEndPoint)
				.param("method", HttpMethod.PUT.name()))//.andDo(print())
				.andExpect(status().is2xxSuccessful());
	}
	
	@Test
	void testOwnerCannotShareWriteUnauthorizedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(authorizeEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
				.param("resourceId", unauthorizedResourceId)
				.param("uri", accessControlShareWriteEndPoint)
				.param("method", HttpMethod.POST.name()))//.andDo(print())
				.andExpect(status().isForbidden());
		
		mockMvc.perform(MockMvcRequestBuilders.get(authorizeEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
				.param("resourceId", unauthorizedResourceId)
				.param("uri", accessControlShareWriteEndPoint)
				.param("method", HttpMethod.PUT.name()))//.andDo(print())
				.andExpect(status().isForbidden());
	}
	
	@Test
	void testUnprivilegedCannotShareWriteAuthorizedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(authorizeEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(unprivilegedUserLogin, unprivilegedUserPassword))
				.param("resourceId", authorizedResourceId)
				.param("uri", accessControlShareWriteEndPoint)
				.param("method", HttpMethod.POST.name()))//.andDo(print())
				.andExpect(status().isForbidden());
		
		mockMvc.perform(MockMvcRequestBuilders.get(authorizeEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(unprivilegedUserLogin, unprivilegedUserPassword))
				.param("resourceId", authorizedResourceId)
				.param("uri", accessControlShareWriteEndPoint)
				.param("method", HttpMethod.PUT.name()))//.andDo(print())
				.andExpect(status().isForbidden());
				
	}
	
	@Test
	void testOwnerCanDisplayAuthorizedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(authorizeEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
				.param("resourceId", authorizedResourceId)
				.param("uri", accessControlDisplayEndPoint)
				.param("method", HttpMethod.GET.name()))//.andDo(print())
				.andExpect(status().is2xxSuccessful());
	}
	
	@Test
	void testOwnerCannotDisplayUnauthorizedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(authorizeEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
				.param("resourceId", unauthorizedResourceId)
				.param("uri", accessControlDisplayEndPoint)
				.param("method", HttpMethod.GET.name()))//.andDo(print())
				.andExpect(status().isForbidden());
	}
	
	@Test
	void testUnprivilegedCannotDisplayAuthorizedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(authorizeEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(unprivilegedUserLogin, unprivilegedUserPassword))
				.param("resourceId", authorizedResourceId)
				.param("uri", accessControlDisplayEndPoint)
				.param("method", HttpMethod.GET.name()))//.andDo(print())
				.andExpect(status().isForbidden());
	}
		
	@Test
	void testOwnerCanEditAuthorizedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(authorizeEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
				.param("resourceId", authorizedResourceId)
				.param("uri", accessControlEditEndPoint)
				.param("method", HttpMethod.POST.name()))//.andDo(print())
				.andExpect(status().is2xxSuccessful());
		
		mockMvc.perform(MockMvcRequestBuilders.get(authorizeEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
				.param("resourceId", authorizedResourceId)
				.param("uri", accessControlEditEndPoint)
				.param("method", HttpMethod.PUT.name()))//.andDo(print())
				.andExpect(status().is2xxSuccessful());
	}
	
	@Test
	void testOwnerCannotEditUnauthorizedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(authorizeEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
				.param("resourceId", unauthorizedResourceId)
				.param("uri", accessControlEditEndPoint)
				.param("method", HttpMethod.POST.name()))//.andDo(print())
				.andExpect(status().isForbidden());
		
		mockMvc.perform(MockMvcRequestBuilders.get(authorizeEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
				.param("resourceId", unauthorizedResourceId)
				.param("uri", accessControlEditEndPoint)
				.param("method", HttpMethod.PUT.name()))//.andDo(print())
				.andExpect(status().isForbidden());
	}
	
	@Test
	void testUnprivilegedCannotEditAuthorizedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(authorizeEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(unprivilegedUserLogin, unprivilegedUserPassword))
				.param("resourceId", authorizedResourceId)
				.param("uri", accessControlEditEndPoint)
				.param("method", HttpMethod.POST.name()))//.andDo(print())
				.andExpect(status().isForbidden());
		
		mockMvc.perform(MockMvcRequestBuilders.get(authorizeEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(unprivilegedUserLogin, unprivilegedUserPassword))
				.param("resourceId", authorizedResourceId)
				.param("uri", accessControlEditEndPoint)
				.param("method", HttpMethod.PUT.name()))//.andDo(print())
				.andExpect(status().isForbidden());
				
	}
	
	
	@Test
	void testOwnerCanFeedbackAuthorizedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(authorizeEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
				.param("resourceId", authorizedResourceId)
				.param("uri", accessControlFeedbackEndPoint)
				.param("method", HttpMethod.POST.name()))//.andDo(print())
				.andExpect(status().is2xxSuccessful());
		
		mockMvc.perform(MockMvcRequestBuilders.get(authorizeEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
				.param("resourceId", authorizedResourceId)
				.param("uri", accessControlFeedbackEndPoint)
				.param("method", HttpMethod.PUT.name()))//.andDo(print())
				.andExpect(status().is2xxSuccessful());
	}
	
	@Test
	void testOwnerCannotFeedbackUnauthorizedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(authorizeEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
				.param("resourceId", unauthorizedResourceId)
				.param("uri", accessControlFeedbackEndPoint)
				.param("method", HttpMethod.POST.name()))//.andDo(print())
				.andExpect(status().isForbidden());
		
		mockMvc.perform(MockMvcRequestBuilders.get(authorizeEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
				.param("resourceId", unauthorizedResourceId)
				.param("uri", accessControlFeedbackEndPoint)
				.param("method", HttpMethod.PUT.name()))//.andDo(print())
				.andExpect(status().isForbidden());
	}
	
	@Test
	void testUnprivilegedCannotFeedbackAuthorizedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(authorizeEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(unprivilegedUserLogin, unprivilegedUserPassword))
				.param("resourceId", authorizedResourceId)
				.param("uri", accessControlFeedbackEndPoint)
				.param("method", HttpMethod.POST.name()))//.andDo(print())
				.andExpect(status().isForbidden());
		
		mockMvc.perform(MockMvcRequestBuilders.get(authorizeEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(unprivilegedUserLogin, unprivilegedUserPassword))
				.param("resourceId", authorizedResourceId)
				.param("uri", accessControlFeedbackEndPoint)
				.param("method", HttpMethod.PUT.name()))//.andDo(print())
				.andExpect(status().isForbidden());
				
	}

	@Test
	void testOwnerCanDeleteAuthorizedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(authorizeEndPoint)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
						.param("resourceId", authorizedResourceId)
						.param("uri", accessControlDeleteEndPoint)
						.param("method", HttpMethod.DELETE.name()))
				.andExpect(status().is2xxSuccessful());
	}

	@Test
	void testOwnerCannotDeleteUnauthorizedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(authorizeEndPoint)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
						.param("resourceId", unauthorizedResourceId)
						.param("uri", accessControlDeleteEndPoint)
						.param("method", HttpMethod.DELETE.name()))
				.andExpect(status().isForbidden());

	}

	@Test
	void testUnprivilegedCannotDeleteAuthorizedResource() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get(authorizeEndPoint)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.header("x-authorization", accessTokenHelper.provideAccessToken(unprivilegedUserLogin, unprivilegedUserPassword))
						.param("resourceId", authorizedResourceId)
						.param("uri", accessControlDeleteEndPoint)
						.param("method", HttpMethod.DELETE.name()))//.andDo(print())
				.andExpect(status().isForbidden());

	}
}
