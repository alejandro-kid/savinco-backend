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
    private ResponseEntity<Map<String, Object>> lastResponse;
}
