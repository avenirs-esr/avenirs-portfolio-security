package fr.avenirsesr.portfolio.security.controller;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import fr.avenirsesr.portfolio.security.AccessTokenHelper;
import fr.avenirsesr.portfolio.security.model.Principal;
import fr.avenirsesr.portfolio.security.model.RBACContext;
import fr.avenirsesr.portfolio.security.services.AccessControlService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;



/**
 *
 * <h1>AccessControlControllerCase2Test</h1>
 * <p>
 * Description:  test case 2. User gribonvald is pair for a resource of type MES.
 * </p>
 * For more details
 * <a href="https://avenirs-esr.github.io/dev-doc/arch-soft-specif-security-rbac-test-case2/">
 *     https://avenirs-esr.github.io/dev-doc/arch-soft-specif-security-rbac-test-case2/
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
		"classpath:db/test-fixtures-rbac-case2.sql"
})
@Transactional
class AccessControlControllerCase2Test {

	@Value("${avenirs.test.rbac.case2.user.login}")
	private String userLogin;
	
	@Value("${avenirs.test.rbac.case2.user.password}")
	private String userPassword;
	
	@Value("${avenirs.test.rbac.case2.authorized.resource.id}")
	private String authorizedResourceId;

    @Value("${avenirs.test.rbac.case2.unauthorized.resource.id}")
    private String unauthorizedResourceId;
	
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
	
	@SpyBean
	private AccessControlService accessControlService;
	
	@Value("${avenirs.test.rbac.case2.application.context.validity.start}")
	private String validityStartString;
	
	@Value("${avenirs.test.rbac.case2.application.context.validity.end}")
	private String validityEndString;
	
	private LocalDateTime validityStart;
	private LocalDateTime beforeValidityStart;
	
	private LocalDateTime validityEnd;
	private LocalDateTime afterValidityEnd;
	
	private LocalDateTime effectiveDateInValidityRange;

	@PostConstruct
	public void init() {
	  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
	  this.validityStart = LocalDate.parse(validityStartString, formatter).atStartOfDay();  
	  this.beforeValidityStart = validityStart.minusDays(1);  
	  this.validityEnd = LocalDate.parse(validityEndString, formatter).atStartOfDay();  
	  this.afterValidityEnd = validityEnd.plusDays(1);  
	  this.effectiveDateInValidityRange = validityStart.plusDays(1);  
	}
	
	@Test
	void testPairCanDisplayAuthorizedResourceOnValidityStartDate() throws Exception {
	  doAnswer(invocation -> {
        Principal principal = invocation.getArgument(0);
        return new RBACContext()
            .setStructures(principal.getStructures())
            .setEffectiveDate(validityStart);  
      }).when(accessControlService).createExecutionContext(any(Principal.class));

	  
	  
		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
				.param("resourceId", authorizedResourceId)
				.param("uri", accessControlDisplayEndPoint)
				.param("method", HttpMethod.GET.name()))//.andDo(print())
				.andExpect(status().is2xxSuccessful());
	}
	
	@Test
    void testPairCannotDisplayAuthorizedResourceBeforeValidityStartDate() throws Exception {
      doAnswer(invocation -> {
        Principal principal = invocation.getArgument(0);
        return new RBACContext()
            .setStructures(principal.getStructures())
            .setEffectiveDate(beforeValidityStart);  
      }).when(accessControlService).createExecutionContext(any(Principal.class));

      
      
        mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
                .param("resourceId", authorizedResourceId)
                .param("uri", accessControlDisplayEndPoint)
                .param("method", HttpMethod.GET.name()))//.andDo(print())
                .andExpect(status().isForbidden());
    }
	
	@Test
    void testPairCanDisplayAuthorizedResourceOnEffectiveDateInValidityRange() throws Exception {
      doAnswer(invocation -> {
        Principal principal = invocation.getArgument(0);
        return new RBACContext()
            .setStructures(principal.getStructures())
            .setEffectiveDate(effectiveDateInValidityRange);  
      }).when(accessControlService).createExecutionContext(any(Principal.class));

      
      
        mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
                .param("resourceId", authorizedResourceId)
                .param("uri", accessControlDisplayEndPoint)
                .param("method", HttpMethod.GET.name()))//.andDo(print())
                .andExpect(status().is2xxSuccessful());
    }
	

    @Test
    void testPairCannotDisplayUnauthorizedResourceOnEffectiveDateInValidityRange() throws Exception {
      doAnswer(invocation -> {
        Principal principal = invocation.getArgument(0);
        return new RBACContext()
            .setStructures(principal.getStructures())
            .setEffectiveDate(effectiveDateInValidityRange);  
      }).when(accessControlService).createExecutionContext(any(Principal.class));

            
        mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
                .param("resourceId", unauthorizedResourceId)
                .param("uri", accessControlDisplayEndPoint)
                .param("method", HttpMethod.GET.name()))//.andDo(print())
                .andExpect(status().isForbidden());
    }
    
	@Test
    void testPairCanDisplayAuthorizedResourceOnValidityEndDate() throws Exception {
      doAnswer(invocation -> {
        Principal principal = invocation.getArgument(0);
        return new RBACContext()
            .setStructures(principal.getStructures())
            .setEffectiveDate(validityEnd);  
      }).when(accessControlService).createExecutionContext(any(Principal.class));

      
      
        mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
                .param("resourceId", authorizedResourceId)
                .param("uri", accessControlDisplayEndPoint)
                .param("method", HttpMethod.GET.name()))//.andDo(print())
                .andExpect(status().is2xxSuccessful());
    }
	
	@Test
    void testPairCannotDisplayAuthorizedResourceAfterValidityEndDate() throws Exception {
      doAnswer(invocation -> {
        Principal principal = invocation.getArgument(0);
        return new RBACContext()
            .setStructures(principal.getStructures())
            .setEffectiveDate(afterValidityEnd);  
      }).when(accessControlService).createExecutionContext(any(Principal.class));

      
      
        mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
                .param("resourceId", authorizedResourceId)
                .param("uri", accessControlDisplayEndPoint)
                .param("method", HttpMethod.GET.name()))//.andDo(print())
                .andExpect(status().isForbidden());
    }
	
	@Test
	void testPairCanFeedbackAuthorizedResourceOnValidityStartDate() throws Exception {
      doAnswer(invocation -> {
        Principal principal = invocation.getArgument(0);
        return new RBACContext()
            .setStructures(principal.getStructures())
            .setEffectiveDate(validityStart);  
      }).when(accessControlService).createExecutionContext(any(Principal.class));

    

		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
				.param("resourceId", authorizedResourceId)
				.param("uri", accessControlFeedbackEndPoint)
				.param("method", HttpMethod.POST.name()))//.andDo(print())
				.andExpect(status().is2xxSuccessful());
		
		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
				.param("resourceId", authorizedResourceId)
				.param("uri", accessControlFeedbackEndPoint)
				.param("method", HttpMethod.PUT.name()))//.andDo(print())
				.andExpect(status().is2xxSuccessful());
	}
	
	@Test
	void testPairCanFeedbackAuthorizedResourceOnEffectiveDateInValidityRange() throws Exception {
      doAnswer(invocation -> {
        Principal principal = invocation.getArgument(0);
        return new RBACContext()
            .setStructures(principal.getStructures())
            .setEffectiveDate(effectiveDateInValidityRange);  
      }).when(accessControlService).createExecutionContext(any(Principal.class));

	  mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
	      .contentType(MediaType.APPLICATION_JSON)
	      .accept(MediaType.APPLICATION_JSON)
	      .header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
	      .param("resourceId", authorizedResourceId)
	      .param("uri", accessControlFeedbackEndPoint)
	      .param("method", HttpMethod.POST.name()))//.andDo(print())
	  .andExpect(status().is2xxSuccessful());
	  
	  mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
	      .contentType(MediaType.APPLICATION_JSON)
	      .accept(MediaType.APPLICATION_JSON)
	      .header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
	      .param("resourceId", authorizedResourceId)
	      .param("uri", accessControlFeedbackEndPoint)
	      .param("method", HttpMethod.PUT.name()))//.andDo(print())
	  .andExpect(status().is2xxSuccessful());
	}
	
	@Test
    void testPairCannotFeedbackUnauthorizedResourceOnEffectiveDateInValidityRange() throws Exception {
      doAnswer(invocation -> {
        Principal principal = invocation.getArgument(0);
        return new RBACContext()
            .setStructures(principal.getStructures())
            .setEffectiveDate(effectiveDateInValidityRange);  
      }).when(accessControlService).createExecutionContext(any(Principal.class));

      mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
          .header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
          .param("resourceId", unauthorizedResourceId)
          .param("uri", accessControlFeedbackEndPoint)
          .param("method", HttpMethod.POST.name()))//.andDo(print())
      .andExpect(status().isForbidden());
      
      mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
          .header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
          .param("resourceId", unauthorizedResourceId)
          .param("uri", accessControlFeedbackEndPoint)
          .param("method", HttpMethod.PUT.name()))//.andDo(print())
      .andExpect(status().isForbidden());
    }
    

    @Test
    void testPairCannotFeedbackAuthorizedResourceBeforeValidityStartDate() throws Exception {
      doAnswer(invocation -> {
        Principal principal = invocation.getArgument(0);
        return new RBACContext()
            .setStructures(principal.getStructures())
            .setEffectiveDate(beforeValidityStart);  
      }).when(accessControlService).createExecutionContext(any(Principal.class));

    

        mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
                .param("resourceId", authorizedResourceId)
                .param("uri", accessControlFeedbackEndPoint)
                .param("method", HttpMethod.POST.name()))//.andDo(print())
                .andExpect(status().isForbidden());
        
        mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
                .param("resourceId", authorizedResourceId)
                .param("uri", accessControlFeedbackEndPoint)
                .param("method", HttpMethod.PUT.name()))//.andDo(print())
                .andExpect(status().isForbidden());
    }
    
    @Test
    void testPairCanFeedbackAuthorizedResourceOnValidityEndDate() throws Exception {
      doAnswer(invocation -> {
        Principal principal = invocation.getArgument(0);
        return new RBACContext()
            .setStructures(principal.getStructures())
            .setEffectiveDate(validityEnd);  
      }).when(accessControlService).createExecutionContext(any(Principal.class));

    

        mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
                .param("resourceId", authorizedResourceId)
                .param("uri", accessControlFeedbackEndPoint)
                .param("method", HttpMethod.POST.name()))//.andDo(print())
                .andExpect(status().is2xxSuccessful());
        
        mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
                .param("resourceId", authorizedResourceId)
                .param("uri", accessControlFeedbackEndPoint)
                .param("method", HttpMethod.PUT.name()))//.andDo(print())
                .andExpect(status().is2xxSuccessful());
    }
    

    @Test
    void testPairCannotFeedbackAuthorizedResourceAfterValidityEndDate() throws Exception {
      doAnswer(invocation -> {
        Principal principal = invocation.getArgument(0);
        return new RBACContext()
            .setStructures(principal.getStructures())
            .setEffectiveDate(afterValidityEnd);  
      }).when(accessControlService).createExecutionContext(any(Principal.class));

    

        mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
                .param("resourceId", authorizedResourceId)
                .param("uri", accessControlFeedbackEndPoint)
                .param("method", HttpMethod.POST.name()))//.andDo(print())
                .andExpect(status().isForbidden());
        
        mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
                .param("resourceId", authorizedResourceId)
                .param("uri", accessControlFeedbackEndPoint)
                .param("method", HttpMethod.PUT.name()))//.andDo(print())
                .andExpect(status().isForbidden());
    }
    
	
	
	@Test
	void testPairCannotDoAnythingElseOnAuthorizedResource() throws Exception {
	  
	  doAnswer(invocation -> {
        Principal principal = invocation.getArgument(0);
        return new RBACContext()
            .setStructures(principal.getStructures())
            .setEffectiveDate(effectiveDateInValidityRange);  
      }).when(accessControlService).createExecutionContext(any(Principal.class));

      
		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
				.param("resourceId", authorizedResourceId)
				.param("uri", accessControlShareReadEndPoint)
				.param("method", HttpMethod.POST.name()))
				.andExpect(status().isForbidden());
		
		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
				.param("resourceId", authorizedResourceId)
				.param("uri", accessControlShareReadEndPoint)
				.param("method", HttpMethod.PUT.name()))
				.andExpect(status().isForbidden());
		
		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
				.param("resourceId", authorizedResourceId)
				.param("uri", accessControlShareWriteEndPoint)
				.param("method", HttpMethod.POST.name()))
				.andExpect(status().isForbidden());
		
		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
				.param("resourceId", authorizedResourceId)
				.param("uri", accessControlShareWriteEndPoint)
				.param("method", HttpMethod.PUT.name()))
				.andExpect(status().isForbidden());
		
		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
				.param("resourceId", authorizedResourceId)
				.param("uri", accessControlEditEndPoint)
				.param("method", HttpMethod.POST.name()))
				.andExpect(status().isForbidden());
		
		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
				.param("resourceId", authorizedResourceId)
				.param("uri", accessControlEditEndPoint)
				.param("method", HttpMethod.PUT.name()))
				.andExpect(status().isForbidden());
		
		mockMvc.perform(MockMvcRequestBuilders.get(accessControlEndPoint)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-authorization", accessTokenHelper.provideAccessToken(userLogin, userPassword))
				.param("resourceId", authorizedResourceId)
				.param("uri", accessControlDeleteEndPoint)
				.param("method", HttpMethod.DELETE.name()))
				.andExpect(status().isForbidden());
		
		
	}

	
	
}
