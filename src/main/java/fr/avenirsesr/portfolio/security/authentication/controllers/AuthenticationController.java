package fr.avenirsesr.portfolio.security.authentication.controllers;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import fr.avenirsesr.portfolio.security.configuration.OIDCConfiguration;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 
 */
@RestController
public class AuthenticationController {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);
	
	@Autowired
	private OIDCConfiguration oidcConfiguration ;
	
	@Value("${avenirs.authentication.oidc.authorise.template.url}")
	private String oidcAuthorizeTemplate;
	
	@Value("${avenirs.authentication.oidc.introspect.url}")
	private String introspectURL;
	
	
	/** Rest client to interact with oidc provider. */
	private RestClient restClient = RestClient.create();
	
	@GetMapping("${avenirs.authentication.oidc.callback}")
	public void oidcCallback(@RequestHeader(value="x-forwarded-host") Optional<String> forwardHost,
			HttpServletResponse response,
			@RequestParam("code") Optional<String> code) throws IOException {
		String host = forwardHost.isEmpty() ?  "localhost" : forwardHost.get();
		String oidcAuthorizeURL = oidcAuthorizeTemplate
				.replaceAll("%HOST%", host)
				.replaceAll("%CODE%","NO_PROVIDED_CODE")
//				.replaceAll("%CODE%", code.orElse("NO_PROVIDED_CODE"))
				.replaceAll("%CLIENT_ID%", oidcConfiguration.getClientId())
				.replaceAll("%CLIENT_SECRET%", oidcConfiguration.getClientSecret());
		LOGGER.debug("oidcCallback code: " + code);
		LOGGER.debug("oidcCallback oidcAuthorizeURL: " + oidcAuthorizeURL);
		String encoded = URLEncoder.encode(oidcAuthorizeURL, "UTF-8");
		response.sendRedirect(oidcAuthorizeURL);
	}
	
	@GetMapping("${avenirs.authentication.oidc.callback.redirect}")
	public void redirect(@RequestHeader(value="Authorization") String authorization,
			@RequestHeader(value="x-forwarded-host") Optional<String> forwardHost,
			HttpServletResponse response) throws IOException{
		String host = forwardHost.isEmpty() ?  "localhost" : forwardHost.get();
	
		final String auth = oidcConfiguration.getClientId() + ":" + oidcConfiguration.getClientSecret();
	}
	
	@PostMapping("${avenirs.authentication.oidc.callback.validate}")
	public String validate(@RequestHeader(value="x-forwarded-host") Optional<String> forwardHost,
			HttpServletResponse response) throws IOException{
		String host = forwardHost.isEmpty() ?  "localhost" : forwardHost.get();
		LOGGER.debug("validate host: " + host);
		String result = restClient.get()
				.uri(introspectURL)
				.retrieve()
				.body(String.class);
				
				LOGGER.warn("oidcConfiguration result: " + result);
		return result;
	}
}
