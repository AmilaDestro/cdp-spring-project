package com.akvelon.cdp.data;

import org.testng.annotations.DataProvider;

public class TestDataProvider {
    @DataProvider
    public Object[][] urlsToRedirectAndNumbersOfRequest() {
        return new Object[][] {
                {"google.com", 1},
                {"habr.com/ru/", 2},
                {"http://sinoptik.ua", 3},
                {"https://spring.io/guides/gs/rest-service/", 4}
        };
    }
}
