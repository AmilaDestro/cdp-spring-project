package com.akvelon.cdp.clients;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.eclipse.jetty.http.HttpMethod.GET;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;

/**
 * Client for HelloController of server application
 */
@Slf4j
public class HelloServiceClient extends AbstractClient {

    private static final String HELLO_URL = "/hello";

    /**
     * Sends GET request to /hello endpoint and receives "Hello World"
     * or "Hello <name>" if name param was specified
     *
     * @param name - username of the user to say hello
     * @return greeting {@link ContentResponse} with greeting string
     */
    public String greeting(final String name) {
        final String query = format("?name=%s", name);
        final String helloUrl = getServerAddress() + HELLO_URL;
        final Request getGreetingRequest = httpClient.newRequest(helloUrl + query)
                                                     .method(GET)
                                                     .timeout(10, SECONDS);
        return executeHttpRequest(getGreetingRequest).getContentAsString();
    }
}
