package com.savinco.financial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FinancialApplication {

    FinancialApplication() {
        // Package-private constructor required by Spring Boot for CGLIB proxy creation
    }

    public static void main(String[] args) {
        SpringApplication.run(FinancialApplication.class, args);
    }
}
