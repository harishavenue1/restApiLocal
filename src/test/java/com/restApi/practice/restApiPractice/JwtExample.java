package encryptJson;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtExample {

    public static void main(String[] args) {
        // Step 1: Create a Nested JSON (A)
        JSONObject jsonA = new JSONObject();
        JSONObject userDetails = new JSONObject();
        userDetails.put("email", "johndoe@example.com");
        userDetails.put("address", "123 Main St");

        JSONObject user = new JSONObject();
        user.put("id", 123);
        user.put("name", "John Doe");
        user.put("roles", new JSONArray(new String[]{"admin", "user"}));
        user.put("details", userDetails);

        jsonA.put("user", user);

        // Convert JSON A to string
        String jsonAStr = jsonA.toString();
        
        System.out.println("JSON to be Encrypted to Token: "+jsonAStr);

        // Step 2: Encrypt JSON A to JWT Token
        SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Generates a strong key

        // Generate JWT token
        String jwtToken = Jwts.builder()
                .setSubject("user_data")
                .claim("data", jsonAStr)
                .setIssuedAt(new Date())
                .signWith(secretKey)
                .compact();

        System.out.println("JWT Token: " + jwtToken);

        // Step 3: Assign JWT Token to New JSON (B)
        JSONObject jsonB = new JSONObject();
        JSONObject metadata = new JSONObject();
        metadata.put("token", jwtToken);

        JSONObject payload = new JSONObject();
        payload.put("info", "Additional data can go here");

        jsonB.put("metadata", metadata);
        jsonB.put("payload", payload);

        System.out.println("JSON B: " + jsonB.toString(2));

        // Step 4: Decrypt the token from JSON B
        String extractedToken = jsonB.getJSONObject("metadata").getString("token");

        // Parse and decrypt the JWT token
        Jws<Claims> jwsClaims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(extractedToken);

        String decryptedJsonStr = jwsClaims.getBody().get("data", String.class);

        System.out.println("Decrypted JSON from Token: " + decryptedJsonStr);

        // Step 5: Validate if the decrypted JSON matches the original JSON A
        JSONObject decryptedJson = new JSONObject(decryptedJsonStr);

        // Compare the JSON objects
        if (jsonObjectsAreEqual(jsonA, decryptedJson)) {
            System.out.println("Validation successful: Decrypted JSON matches the original JSON A.");
        } else {
            System.out.println("Validation failed: Decrypted JSON does not match the original JSON A.");
        }
    }

    // Utility method to compare two JSONObjects
    private static boolean jsonObjectsAreEqual(JSONObject json1, JSONObject json2) {
        return json1.similar(json2);
    }
}
