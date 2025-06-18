package org.formation.cibabackend;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CibaContextStorage {
    private final Map<String, String> tokenMap = new ConcurrentHashMap<>();

    public void storeToken(String loginHint, String token) {
        tokenMap.put(loginHint, token);
    }

    public String getToken(String loginHint) {
        return tokenMap.get(loginHint);
    }

    public void clear(String loginHint) {
        tokenMap.remove(loginHint);
    }
}
