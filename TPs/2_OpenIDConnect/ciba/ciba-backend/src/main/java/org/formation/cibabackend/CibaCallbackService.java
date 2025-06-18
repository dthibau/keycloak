package org.formation.cibabackend;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class CibaCallbackService {

    @Async
    public void sendAuthenticationResultToKeycloak(String bearerToken, String realm, String status) {
        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:8080/realms/" + realm + "/protocol/openid-connect/ext/ciba/auth/callback";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", bearerToken);

        Map<String, String> body = Map.of("status", status);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

        try {
            Thread.sleep(10);
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            System.out.println("Envoi du résultat de l'authentification à Keycloak : " + response.getStatusCode());
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi du résultat CIBA : " + e.getMessage());
        }
    }
}
