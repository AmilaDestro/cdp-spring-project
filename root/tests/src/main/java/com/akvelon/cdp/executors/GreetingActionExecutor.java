package com.akvelon.cdp.executors;

import static java.util.concurrent.TimeUnit.SECONDS;

import com.akvelon.cdp.clients.HelloServiceClient;
import lombok.AllArgsConstructor;
import lombok.val;

@AllArgsConstructor
public class GreetingActionExecutor extends AbstractActionExecutor {

    private final HelloServiceClient helloServiceClient;

    public String sendUserNameAndGetHelloMessage(final String userName) {
        val response = executeTaskAndWaitForResponse(() -> helloServiceClient.greeting(userName), 15, SECONDS);
        return (String) response;
    }
}
