package com.akvelon.cdp;

import static java.lang.String.format;

import com.akvelon.cdp.data.TestDataProvider;
import com.akvelon.cdp.executors.GreetingActionExecutor;
import lombok.val;
import org.testng.annotations.Test;

/**
 * Contains tests related to Greeting Service of the server application
 */
public class HelloServiceQaIT extends QaBase {

    private final GreetingActionExecutor greetingActionExecutor;

    public HelloServiceQaIT() {
        greetingActionExecutor = new GreetingActionExecutor(helloServiceClient);
    }

    @Test(dataProvider = "usersAndExpectedHelloMessage", dataProviderClass = TestDataProvider.class)
    public void testHelloMessageLogic(final String userName, final String expectedMessage) {
        val actualMessage = greetingActionExecutor.sendUserNameAndGetHelloMessage(userName);
        softAssert.assertNotNull(actualMessage, "Greeting service returned null instead of hello message");
        softAssert.assertEquals(actualMessage, expectedMessage, format("Expected message - %s, actual - %s",
                                                                       expectedMessage, actualMessage));
        softAssert.assertAll();
    }
}
