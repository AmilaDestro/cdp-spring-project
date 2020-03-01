package com.akvelon.cdp.data;

import org.testng.annotations.DataProvider;

public class TestDataProvider {
    @DataProvider
    public Object[][] urlsToRedirectAndNumbersOfRequest() {
        return new Object[][] {
                {"google.com", 1},
                {"habr.com/ru/", 1},
                {"http://sinoptik.ua", 1},
                {"https://rozetka.com.ua/", 1}
        };
    }

    @DataProvider
    public Object[][] twoDifferentUrls() {
        return new Object[][] {
                {"http://google.com/", "https://dou.ua"},
        };
    }

    @DataProvider
    public Object[][] applicationHomePageUrl() {
        return new Object[][] {
                {"http://localhost:8081/hello"}
        };
    }

    @DataProvider
    public Object[][] singleUrl() {
        return new Object[][] {
                {"https://start.spring.io/"}
        };
    }

    @DataProvider
    public Object[][] usersAndExpectedHelloMessage() {
        return new Object[][] {
                {"", "Hello World"},
                {"Liudmila", "Hello Liudmila"},
        };
    }
}
