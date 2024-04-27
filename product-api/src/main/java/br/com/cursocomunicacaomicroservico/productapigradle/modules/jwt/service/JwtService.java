package br.com.cursocomunicacaomicroservico.productapigradle.modules.jwt.service;



import br.com.cursocomunicacaomicroservico.productapigradle.config.exception.AuthenticateException;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.jwt.dto.JwtResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class JwtService {

    private static final String BEARER = "bearer ";

    @Value("${app-config.secrets.api-secret}")
    private String apiSecret;

    public void validateAuthorization(String token) {
        try {
            var accessToken = extractToken(token);
            var claims = Jwts.parser()
                    .setSigningKey(Keys.hmacShaKeyFor(apiSecret.getBytes()))
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
            var user = JwtResponse.getUser(claims);
            if (isEmpty(user) || isEmpty(user.getId())) {
                throw new AuthenticateException("User is not valid.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new AuthenticateException("Error while trying to proccess the access token.");
        }
    }

    private String extractToken(String token) {
        if (isEmpty(token)) {
            throw new AuthenticateException("Access token was not informed.");
        }
        if (token.toLowerCase().contains(BEARER)) {
            token = token.toLowerCase();
            token = token.replace(BEARER, Strings.EMPTY);
        }
        return token;
    }
}
