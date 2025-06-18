package org.formation.cibabackend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
public class CibaController {

    @Autowired
    private CibaCallbackService cibaCallbackService;

    @Autowired
    private CibaContextStorage storage;

    @PostMapping("/")
    public ResponseEntity<Void> receiveCibaRequest(
            @RequestBody CibaRequest request,
            @RequestHeader("Authorization") String bearerToken
    ) {
        System.out.println("Requête CIBA reçue : " + request);
        System.out.println("Bearer is : " + bearerToken);

        // TODO : stocker temporairement le bearerToken pour l’étape 2
        // TODO : déclencher un processus d’authentification asynchrone

        storage.storeToken(request.getLogin_hint(), bearerToken);
        return ResponseEntity.status(HttpStatus.CREATED).build(); // 201
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmAuth(@RequestParam String login_hint,
                                              @RequestParam(defaultValue = "SUCCEED") String status) {
        String token = storage.getToken(login_hint);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Aucun token trouvé pour ce login_hint");
        }

        cibaCallbackService.sendAuthenticationResultToKeycloak(token, "formation", status);
        storage.clear(login_hint);
        return ResponseEntity.ok("Résultat envoyé à Keycloak : " + status);
    }
}
