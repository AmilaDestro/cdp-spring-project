package com.akvelon.cdp.clients;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Client for HelloController of server application
 */
public class HelloServiceClient extends AbstractClient {
    private static final String HELLO_URL = "/hello";

    /**
     * Sends GET request to /hello endpoint and receives "Hello World"
     * or "Hello <name>" if name param was specified
     *
     * @param name - username of the user to say hello
     * @return greeting {@link String}
     * @throws IOException
     * @throws URISyntaxException
     */
    public String greeting(final String name) throws IOException, URISyntaxException {
        final String helloUri = getServerAddress() + HELLO_URL;
        final URIBuilder uriBuilder = new URIBuilder(helloUri);
        uriBuilder.setParameter("name", name);

        final HttpGet httpGet = new HttpGet(uriBuilder.build());
        final CloseableHttpResponse response = getHttpClient().execute(httpGet);

        return mapResponseToJson(response);
    }
}
