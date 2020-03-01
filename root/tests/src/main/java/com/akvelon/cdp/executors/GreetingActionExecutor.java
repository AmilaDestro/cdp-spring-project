package com.akvelon.cdp.executors;

import static java.util.concurrent.TimeUnit.SECONDS;

import com.akvelon.cdp.clients.HelloServiceClient;
import lombok.AllArgsConstructor;
import lombok.val;

/**
 * Contains methods to work with Greeting Service
 */

@AllArgsConstructor
public class GreetingActionExecutor extends AbstractActionExecutor {

    private final HelloServiceClient helloServiceClient;

    /**
     * Sends username to endpoint /hello and waits for a response with greeting message
     *
     * @param userName of user to say hello to
     * @return hello message as {@link String}
     */
    public String sendUserNameAndGetHelloMessage(final String userName) {
        val response = executeTaskAndWaitForResponse(() -> helloServiceClient.greeting(userName), 15, SECONDS);
        return (String) response;
    }
}
