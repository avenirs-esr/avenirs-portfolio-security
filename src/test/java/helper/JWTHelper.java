/**
 * 
 */
package helper;

import java.util.Base64;

/**
 * Test helper for JWT
 */
public class JWTHelper {
	
	public  String invalidatePayload(String jwt, String toReplace, String newValue) {
        String[] parts = jwt.split("\\.");

        // Get the payload
        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
        System.out.println("Payload original: " + payloadJson);

        // Changes the payload so it becomes invalid regarding its signature.
        String modifiedPayloadJson = payloadJson.replace(toReplace, newValue);
        System.out.println("Payload modifié: " + modifiedPayloadJson);
        
        // REbuild the jwt
        String modifiedPayloadBase64 = Base64.getUrlEncoder().withoutPadding().encodeToString(modifiedPayloadJson.getBytes());
        return parts[0] + "." + modifiedPayloadBase64 + "." + parts[2];

    }

}
