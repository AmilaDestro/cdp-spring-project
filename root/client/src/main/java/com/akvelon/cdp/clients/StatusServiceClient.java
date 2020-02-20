package com.akvelon.cdp.clients;

import com.akvelon.cdp.entitieslib.RequestStatus;
import lombok.val;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.net.URISyntaxException;

public class StatusServiceClient extends AbstractClient {
    private static final String STATUS_URL = "/status";

    public RequestStatus getStatus() throws URISyntaxException, IOException {
        final String statusUri = getServerAddress() + STATUS_URL;

        URIBuilder uriBuilder = new URIBuilder(statusUri);
        HttpGet httpGet = new HttpGet(uriBuilder.build());

        CloseableHttpResponse response = getHttpClient().execute(httpGet);
        val json = mapResponseToJson(response);

        return mapJsonToObject(json, RequestStatus.class);
    }
}
