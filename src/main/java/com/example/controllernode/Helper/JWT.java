package com.example.controllernode.Helper;

import com.example.controllernode.Model.Role;
import com.example.controllernode.Model.User;
import com.example.controllernode.Services.IServices.IUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

public class JWT {

    @Autowired
    static IUserService userService;

    private static String SECRET_KEY = "oeRaYY7Wo24sDqKSX3IM9ASGmdGPmkTd9jo1QTy4b7P9Ze5_9hKolVX8xNrQDcNRfVEdTZNOuOyqEGhXEbdJI-ZQ19k_o9MI0y3eZN2lp9jow55FfXMiINEdt1XR85VipRLSOkT6kSpzs2x-jbLDiz9iFVzkd81YKxMgPA7VfZeQUm4n-mOmnWMaVX30zGFU4L3oPBctYKkl4dYfqYWqRNfrgPJVi5DGFjywgxx0ASEiJHtV72paI3fDR2XwlSkyhhmY-ICjCRmsJN4fX1pdoL8a18-aQrvyu4j0Os6dVPYIoPvvY0SAZtWYKHfM15g7A3HD4cVREf9cUsprCRK93w";
    private static String SECRET_KEY_FOR_NODE= "My Key";
    public static String createJWT(String username,String role, String issuer) {

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        JwtBuilder builder = Jwts.builder()
                .setIssuedAt(now)
                .setIssuer(issuer)
                .signWith(signatureAlgorithm, signingKey)
                .claim("username",username)
                .claim("role",role);

        return builder.compact();
    }

    public static String createJWTWithDatabase(String database,String nodeUrl) {

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        JwtBuilder builder = Jwts.builder()
                .setIssuedAt(now)
                .signWith(signatureAlgorithm, signingKey)
                .claim("username",CurrentUser.getUser().getUsername())
                .claim("role",CurrentUser.getUser().getRole())
                .claim("database",database)
                .claim("baseUrl",nodeUrl);


        return builder.compact();
    }

    public static String createJWTForNode() {

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY_FOR_NODE);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        JwtBuilder builder = Jwts.builder()
                .signWith(signatureAlgorithm, signingKey)
                .claim("Server","ControllerNode");

        return builder.compact();
    }

    public static void validate(String token){
        try {
            var claims=decodeJWT(token);

            var username = (String) claims.get("username");
            var role = (String) claims.get("role");
            if(username == null || role == null){
                CurrentUser.setUser(null);
                return;
            }

            var database = (String) claims.get("database");
            var baseUrl = (String) claims.get("baseUrl");

            var currentBaseUrl= ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            if(database != null && baseUrl != null && baseUrl.equals(currentBaseUrl)){
                CurrentUser.setDatabase(database);
            } else
                CurrentUser.setDatabase("");

            CurrentUser.setUser(new User(username,"", Role.valueOf(role)));
        }catch (Exception ex){
            CurrentUser.setUser(null);
            CurrentUser.setDatabase("");
        }
    }
    public static boolean validateNodeJWT(String token){
        try {
            var claims=decodeJWTFromNode(token);

            var Server = (String) claims.get("Server");

            if(Server != null && Server.equals("Node")){
                return true;
            }

            return false;
        }catch (Exception ex){
            return false;
        }
    }

    public static Claims decodeJWT(String jwt) {
        Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
                .parseClaimsJws(jwt).getBody();
        return claims;
    }
    public static Claims decodeJWTFromNode(String jwt) {
        Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY_FOR_NODE))
                .parseClaimsJws(jwt).getBody();
        return claims;
    }

}
