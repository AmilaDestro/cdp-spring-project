package com.akvelon.cdp.clients;

import com.akvelon.cdp.entitieslib.Request;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import lombok.Generated;
import lombok.val;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RequestServiceClient extends AbstractClient {
    private static final String REDIRECT_URL = "/redirect";
    private static final String REQUEST_BY_ID_URL = "/requests/%s";
    private static final String GET_ALL_REQUESTS_URL = "/requests";
    private static final String GET_LAST_REQUEST_URL = GET_ALL_REQUESTS_URL + "/last";

    public boolean redirectToSpecifiedUrlAndUpdateStatistic(final String url) throws URISyntaxException, IOException {
        final String redirectUri = getServerAddress() + REDIRECT_URL;

        URIBuilder uriBuilder = new URIBuilder(redirectUri);
        uriBuilder.setParameter("url", url);

        HttpGet httpGet = new HttpGet(uriBuilder.build());
        CloseableHttpResponse response = getHttpClient().execute(httpGet);
        int statusCode = response.getStatusLine().getStatusCode();

        return statusCode >= 200 && statusCode < 300;
    }

    public Request getRequestById(final long requestId) throws IOException {
        final String requestByIdUri = getServerAddress() + String.format(REQUEST_BY_ID_URL, requestId);

        HttpGet httpGet = new HttpGet(requestByIdUri);
        CloseableHttpResponse response = getHttpClient().execute(httpGet);
        val jsonResponse = mapResponseToJson(response);

        return mapJsonToObject(jsonResponse, Request.class);
    }

    public Request getLastRequest() throws IOException {
        final String lastRequestUri = getServerAddress() + GET_LAST_REQUEST_URL;

        HttpGet httpGet = new HttpGet(lastRequestUri);
        CloseableHttpResponse response = getHttpClient().execute(httpGet);
        val jsonResponse = mapResponseToJson(response);

        return mapJsonToObject(jsonResponse, Request.class);
    }

    public List<Request> getAllRequests() throws IOException {
        final String getRequestsUri = getServerAddress() + GET_ALL_REQUESTS_URL;

        HttpGet httpGet = new HttpGet(getRequestsUri);
        CloseableHttpResponse response = getHttpClient().execute(httpGet);
        val jsonResponse = mapResponseToJson(response);

        return mapJsonToRequestsList(jsonResponse);
    }

    private List<Request> mapJsonToRequestsList(final String json) throws JsonProcessingException {
        List<Request> requests = new ArrayList<>();

        Gson gson = new Gson();
        JsonArray jsonArray = (JsonArray) JsonParser.parseString(json);
        jsonArray.forEach(
                jsonElement -> requests.add(gson.fromJson(jsonElement, Request.class))
        );
        return requests;
    }
}
