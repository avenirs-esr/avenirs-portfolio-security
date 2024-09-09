package fr.avenirsesr.portfolio.security.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.avenirsesr.portfolio.security.configuration.OIDCConfiguration;

@Service
public class AccessTokenService {
	

	/** OIDC settings */
	@Autowired
	private OIDCConfiguration oidcConfiguration;
	

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(AccessTokenService.class);
	
    private static final String ACCESS_TOKEN_END_POINT = "http://localhost/cas/oidc/accessToken";
    private static final String TOKEN_URL = "https://localhost/cas/oauth2.0/token";
    
    private static final String TICKET_URL = "https://localhost/cas/v1/tickets";
    
    private static final String SCOPE = "profile";

    public String getTGT(String username, String password) throws Exception {
       
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(TICKET_URL);

         StringEntity params = new StringEntity("username=" + username + "&password=" + password);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");
        post.setEntity(params);

        
        HttpResponse response = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        
        String line;
        String tgtUrl = null;
        while ((line = rd.readLine()) != null) {
            if (line.contains("Location")) {
                tgtUrl = line.substring(line.indexOf("Location: ") + 10).trim();
            }
        }

        // Fermeture des ressources
        client.close();

        // Retourne le TGT URL
        return tgtUrl;
    }
    public  String getServiceTicket(String tgtUrl, String serviceUrl) throws Exception {
        // Configuration du client HTTP
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(tgtUrl);

        // Préparation des paramètres (service URL)
        StringEntity params = new StringEntity("service=" + serviceUrl);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");
        post.setEntity(params);

        // Exécuter la requête
        HttpResponse response = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        // Lecture de la réponse pour obtenir le Service Ticket
        String st = rd.readLine();

        // Fermeture des ressources
        client.close();

        // Retourne le Service Ticket
        return st;
    }
    public static String validateServiceTicket(String serviceUrl, String serviceTicket) throws Exception {
        // URL de validation du ticket
        String validateUrl = "https://<cas_base_url>/serviceValidate?service=" + serviceUrl + "&ticket=" + serviceTicket;

        // Configuration du client HTTP
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(validateUrl);

        // Exécuter la requête
        HttpResponse response = client.execute(get);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        // Lecture de la réponse pour obtenir l'access token ou les informations d'authentification
        String line;
        StringBuilder result = new StringBuilder();
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        // Fermeture des ressources
        client.close();

        // Retourne la réponse contenant l'access token
        return result.toString();
    }
    
    
    
    public  void getUserAccessToken(String login, String password) throws Exception {
        
        String serviceUrl = "https://votre_service_url";

        // Étape 1 : Obtenir le TGT
        String tgt = getTGT(login, password);
        System.out.println("TGT: " + tgt);

        // Étape 2 : Obtenir le Service Ticket
        String serviceTicket = getServiceTicket(tgt, serviceUrl);
        System.out.println("Service Ticket: " + serviceTicket);

        // Étape 3 : Valider le Service Ticket pour obtenir l'Access Token
        String accessToken = validateServiceTicket(serviceUrl, serviceTicket);
        System.out.println("Access Token: " + accessToken);
    }
    
    
    public String getAccessToken(String username, String password) throws Exception {
        // URL de l'endpoint OAuth 2.0
        String oauthUrl = ACCESS_TOKEN_END_POINT;

        // Configuration du client HTTP
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(oauthUrl);

        // Préparation des paramètres pour OAuth 2.0 (Resource Owner Password Credentials Grant)
        StringEntity params = new StringEntity(
            "grant_type=password&client_id=" + oidcConfiguration.getClientId() +
            "&client_secret=" + oidcConfiguration.getClientSecret() +
            "&username=" + username +
            "&password=" + password +
            "&redirect_uri=https://localhost/apisix-gw/oidc/callback"
        );
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");
        post.setEntity(params);

        // Exécuter la requête
        HttpResponse response = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        // Lecture de la réponse pour obtenir l'access token
        String line;
        StringBuilder result = new StringBuilder();
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        // Fermeture des ressources
        client.close();

        // Retourne la réponse JSON contenant l'access token
        return result.toString();
    }
    
    
//    public  void getAccessToken() {
//        WebClient webClient = WebClient.builder()
//                .baseUrl(TOKEN_URL)
//                .defaultHeader("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//                .build();
//
//        Mono<String> response = webClient.post()
//                .uri(uriBuilder -> uriBuilder
//                        .queryParam("grant_type", "client_credentials")
//                        .queryParam("client_id", oidcConfiguration.getClientId())
//                        .queryParam("client_secret", oidcConfiguration.getClientSecret())
//                        .queryParam("scope", SCOPE)
//                        .build())
//                .retrieve()
//                .bodyToMono(String.class);
//
//        response.subscribe(token -> {
//            System.out.println("Access Token: " + token);
//        });
//    }
    
    
}
