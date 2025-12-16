package com.savinco.financial.bdd.support;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.savinco.financial.domain.repository.CountryRepository;
import com.savinco.financial.domain.repository.CurrencyRepository;
import com.savinco.financial.domain.repository.FinancialDataRepository;

import io.cucumber.java.Before;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.RequiredArgsConstructor;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@RequiredArgsConstructor
public class CucumberHooks {

    private final FinancialDataRepository financialDataRepository;
    private final CurrencyRepository currencyRepository;
    private final CountryRepository countryRepository;

    @Before
    @Transactional
    public void setUp() {
        // Clean database before each scenario
        // Order matters: delete in reverse dependency order
        financialDataRepository.deleteAll();
        countryRepository.deleteAll();
        currencyRepository.deleteAll();
    }
}
