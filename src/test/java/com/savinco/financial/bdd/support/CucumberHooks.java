package com.savinco.financial.bdd.support;

import io.cucumber.java.Before;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CucumberHooks {

    @Before
    public void setUp() {
        // Setup before each scenario
        // Spring Boot Test context is automatically initialized
    }
}
