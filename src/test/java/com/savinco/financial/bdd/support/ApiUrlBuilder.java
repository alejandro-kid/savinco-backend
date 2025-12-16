package com.savinco.financial.bdd.support;

import org.springframework.stereotype.Component;

@Component
public class ApiUrlBuilder {

    private static final String PROTOCOL = "http";
    private static final String DEFAULT_HOST = "localhost";
    private static final String BASE_PATH = "/api/v1";
    private static final String HEALTH_ENDPOINT = BASE_PATH + "/health";
    private static final String FINANCIAL_DATA_ENDPOINT = BASE_PATH + "/financial-data";
    private static final String CURRENCY_ENDPOINT = BASE_PATH + "/currencies";
    private static final String COUNTRY_ENDPOINT = BASE_PATH + "/countries";

    private String getHost() {
        return System.getenv("TEST_API_HOST") != null 
            ? System.getenv("TEST_API_HOST") 
            : DEFAULT_HOST;
    }

    public String buildHealthUrl(int port) {
        return buildUrl(port, HEALTH_ENDPOINT);
    }

    public String buildFinancialDataUrl(int port) {
        return buildUrl(port, FINANCIAL_DATA_ENDPOINT);
    }

    public String buildFinancialDataUrl(int port, String countryCode) {
        return buildUrl(port, FINANCIAL_DATA_ENDPOINT + "/" + countryCode);
    }

    public String buildFinancialDataSummaryUrl(int port) {
        return buildUrl(port, FINANCIAL_DATA_ENDPOINT + "/summary");
    }

    public String buildCurrencyUrl(int port) {
        return buildUrl(port, CURRENCY_ENDPOINT);
    }

    public String buildCurrencyUrl(int port, String code) {
        return buildUrl(port, CURRENCY_ENDPOINT + "/" + code);
    }

    public String buildCurrencyBaseUrl(int port) {
        return buildUrl(port, CURRENCY_ENDPOINT + "/base");
    }

    public String buildCurrencyExchangeRateUrl(int port, String code) {
        return buildUrl(port, CURRENCY_ENDPOINT + "/" + code + "/exchange-rate");
    }

    public String buildCountryUrl(int port) {
        return buildUrl(port, COUNTRY_ENDPOINT);
    }

    public String buildCountryUrl(int port, String code) {
        return buildUrl(port, COUNTRY_ENDPOINT + "/" + code);
    }

    private String buildUrl(int port, String path) {
        return String.format("%s://%s:%d%s", PROTOCOL, getHost(), port, path);
    }
}
