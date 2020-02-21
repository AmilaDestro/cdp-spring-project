package com.akvelon.cdp.clients;

import com.akvelon.cdp.entitieslibrary.Request;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.val;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

/**
 * Client for {@link Request} entity service
 */
public class RequestServiceClient extends AbstractClient {
    private static final String REDIRECT_URL = "/redirect";
    private static final String REQUEST_BY_ID_URL = "/requests/%s";
    private static final String GET_ALL_REQUESTS_URL = "/requests";
    private static final String GET_LAST_REQUEST_URL = GET_ALL_REQUESTS_URL + "/last";

    /**
     * Performs redirect to specified URL/URI, collects statistics, updates status and returns true if these actions
     * were successful
     *
     * @param url - URL to redirect to
     * @return true is all required by services actions were successful
     * @throws URISyntaxException
     * @throws IOException
     */
    public boolean redirectToSpecifiedUrlAndUpdateStatistic(final String url) throws URISyntaxException, IOException {
        final String redirectUri = getServerAddress() + REDIRECT_URL;
        final URIBuilder uriBuilder = new URIBuilder(redirectUri);
        uriBuilder.setParameter("url", url);

        final HttpGet httpGet = new HttpGet(uriBuilder.build());
        val response = getHttpClient().execute(httpGet);
        final int statusCode = response.getStatusLine().getStatusCode();

        return statusCode >= 200 && statusCode < 300;
    }

    /**
     * Gets {@link Request} by id
     *
     * @param requestId of request entity that must be returned
     * @return found request
     * @throws IOException
     */
    public Request getRequestById(final long requestId) throws IOException {
        final String requestByIdUri = getServerAddress() + String.format(REQUEST_BY_ID_URL, requestId);
        final HttpGet httpGet = new HttpGet(requestByIdUri);
        val httpResponse = getHttpClient().execute(httpGet);
        val jsonResponse = mapResponseToJson(httpResponse);

        return mapJsonToObject(jsonResponse, Request.class);
    }

    /**
     * Gets record about last made request
     * @return the last {@link Request}
     * @throws IOException
     */
    public Request getLastRequest() throws IOException {
        final String lastRequestUri = getServerAddress() + GET_LAST_REQUEST_URL;
        final HttpGet httpGet = new HttpGet(lastRequestUri);
        val httpResponse = getHttpClient().execute(httpGet);
        val jsonResponse = mapResponseToJson(httpResponse);

        return mapJsonToObject(jsonResponse, Request.class);
    }

    /**
     * Gets all existing {@link Request}
     *
     * @return {@link List<Request>}
     * @throws IOException
     */
    public List<Request> getAllRequests() throws IOException {
        final String getRequestsUri = getServerAddress() + GET_ALL_REQUESTS_URL;
        final HttpGet httpGet = new HttpGet(getRequestsUri);
        val httpResponse = getHttpClient().execute(httpGet);
        val jsonResponse = mapResponseToJson(httpResponse);

        return mapJsonToRequestsList(jsonResponse);
    }

    /**
     * Deletes request with the given id
     *
     * @param requestId of {@link Request} that must be deleted
     * @return true if request with the given id was deleted
     * @throws IOException
     */
    public boolean deleteRequestById(final long requestId) throws IOException {
        final String deleteRequestUri = getServerAddress() + String.format(REQUEST_BY_ID_URL, requestId);
        final HttpDelete httpDelete = new HttpDelete(deleteRequestUri);
        val response = httpClient.execute(httpDelete);

        return Boolean.parseBoolean(mapResponseToJson(response));
    }

    /**
     * Maps json array to list of requests
     *
     * @param json to process
     * @return {@link List<Request>}
     * @throws JsonProcessingException
     */
    private List<Request> mapJsonToRequestsList(final String json) throws JsonProcessingException {
        final Request[] requestsArray = objectMapper.readValue(json, Request[].class);
        return Arrays.asList(requestsArray);
    }
}
