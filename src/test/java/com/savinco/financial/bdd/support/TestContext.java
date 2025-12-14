package com.savinco.financial.bdd.support;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Getter
@Setter
public class TestContext {
    private ResponseEntity<?> lastResponse;

    @SuppressWarnings("unchecked")
    public Map<String, Object> getLastResponseBodyAsMap() {
        if (lastResponse == null || lastResponse.getBody() == null) {
            return null;
        }
        return (Map<String, Object>) lastResponse.getBody();
    }
}
