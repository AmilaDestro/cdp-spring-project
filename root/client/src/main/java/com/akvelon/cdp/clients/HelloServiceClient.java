package com.akvelon.cdp.clients;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.net.URISyntaxException;

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
     * @return greeting {@link String}
     */
    public String greeting(final String name) {
        final String helloUri = getServerAddress() + HELLO_URL;
        try {
            final URIBuilder uriBuilder = new URIBuilder(helloUri);
            uriBuilder.setParameter("name", name);

            final HttpGet httpGet = new HttpGet(uriBuilder.build());
            final CloseableHttpResponse response = getHttpClient().execute(httpGet);

            return mapResponseToJson(response);
        } catch (IOException | URISyntaxException e) {
            log.error(ERROR_MESSAGE, e.getMessage());
        }
        return null;
    }
}
