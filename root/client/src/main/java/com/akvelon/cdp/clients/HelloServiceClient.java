package com.akvelon.cdp.clients;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.net.URISyntaxException;

@Slf4j
public class HelloServiceClient extends AbstractClient {
    private static final String HELLO_URL = "/hello";

    public String greeting(final String name) throws IOException, URISyntaxException {
        final String helloUri = getServerAddress() + HELLO_URL;

        URIBuilder uriBuilder = new URIBuilder(helloUri);
        uriBuilder.setParameter("name", name);

        HttpGet httpGet = new HttpGet(uriBuilder.build());
        CloseableHttpResponse response = getHttpClient().execute(httpGet);

        return mapResponseToJson(response);
    }
}
