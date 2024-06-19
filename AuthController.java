package fr.avenirsesr.avenirsapi.auth.web;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import fr.avenirsesr.avenirsapi.auth.model.OIDCSettings;
import fr.avenirsesr.avenirsapi.health.model.HealthCheck;
import fr.avenirsesr.avenirsapi.notification.model.Notification;
import fr.avenirsesr.avenirsapi.notification.service.NotificationService;
import lombok.Getter;

@RestController
public class AuthController {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	private OIDCSettings oidcSettings;
	
	/** Introspect end point. */
	@Getter
	@Value("${avenirs.oidc.introspect.endpoint}")
	private String introspectEndPoint;
	
	/** Profile end point. */
	@Getter
	@Value("${avenirs.oidc.profile.endpoint}")
	private String profileEndPoint;
	
	private int counter = 1;

	@Autowired
	private NotificationService service;
	
	
	@PostMapping("${avenirs.routes.auth.validate}")
	public String getNotifications(){
		LOGGER.info("Get h");
		return "OK";
	}
	
//	@GetMapping("${avenirs.routes.auth.validate}")
	@PostMapping("h2")
	public void validate(@RequestHeader Map<String, String> headers){
		LOGGER.trace("Post validate");
		final String header = headers.get("x-forwarded-host");
		final String host = StringUtils.hasLength(header) ? header : "localhost";
		final String token = headers.get("x-authorization");
		LOGGER.trace("Post validate token: " + token);
		
		
		
		final String auth = oidcSettings.getCasClientId() + ":" + oidcSettings.getCasClientSecret();
		
		LOGGER.trace("Post validate oidcSettings.getCasClientId(): " + oidcSettings.getCasClientId());
		LOGGER.trace("Post validate oidcSettings.getCasClientSecret(): " + oidcSettings.getCasClientSecret());
		LOGGER.trace("Post validate auth: " + auth);
		
		
        final byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(Charset.forName("US-ASCII")));
        final String authHeader = "Basic " + new String( encodedAuth );
        
        final HttpHeaders queryHeaders = new HttpHeaders();
		 queryHeaders.setContentType(MediaType.APPLICATION_JSON);
	        queryHeaders.set("Authorization", authHeader);
	        
queryHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        //final  String reqBody = "{\"access_token\": \"" + token +"\"}";
        Map<String, String> requestBody = new HashMap();
        requestBody.put("token", token);
        LOGGER.trace("Post validate requestBody: " + requestBody);
        
        final HttpEntity<Map<String, String>> query = new HttpEntity<Map<String, String>>(requestBody, queryHeaders);
        LOGGER.trace("Post validate query: " + query);
        
        
        
        final RestTemplate restTemplate = new RestTemplate();
       
        
        ResponseEntity<String> response =        restTemplate.postForEntity(
        		introspectEndPoint, 
        		query, 
        		String.class);
//        ResponseEntity<String> response =        restTemplate.exchange(
//        		introspectEndPoint, 
//        		HttpMethod.POST, 
//        		new HttpEntity<String>(reqBody, queryHeaders), 
//        		String.class);
        
        LOGGER.trace("=====" + response);
        
		//url = `${uri}?client_id=${cas_client_id}&client_secret=${cas_client_secret}&redirect_uri=http://${host}/node-api/cas-auth-callback/access&code=${sessionCode}&scope=openid profile email&response_type=token`
			 
			
	}
	
//	@GetMapping("${avenirs.routes.auth.validate}")
//	public void validate2(@RequestHeader Map<String, String> headers,@RequestBody Map<String,String> body){
//		final String header = headers.get("x-forwarded-host");
//		final String host = StringUtils.hasLength(header) ? header : "localhost";
//		final String token = headers.get("x-authorization");
//		final String url = introspectEndPoint 
//				+ "?client_id=" + oidcSettings.getCasClientId()
//				+ "&client_secret="+oidcSettings.getCasClientSecret();
//		
//		
//		//url = `${uri}?client_id=${cas_client_id}&client_secret=${cas_client_secret}&redirect_uri=http://${host}/node-api/cas-auth-callback/access&code=${sessionCode}&scope=openid profile email&response_type=token`
//			 
//			
//	}
	
	

}
