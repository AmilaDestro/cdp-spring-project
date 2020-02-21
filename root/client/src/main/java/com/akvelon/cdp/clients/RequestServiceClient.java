package com.akvelon.cdp.clients;

import com.akvelon.cdp.entitieslibrary.Request;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Client for {@link Request} entity service
 */
@Slf4j
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
     */
    public boolean redirectToSpecifiedUrlAndUpdateStatistic(final String url) {
        final String redirectUri = getServerAddress() + REDIRECT_URL;
        try {
            final URIBuilder uriBuilder = new URIBuilder(redirectUri);
            uriBuilder.setParameter("url", url);

            final HttpGet httpGet = new HttpGet(uriBuilder.build());
            val response = getHttpClient().execute(httpGet);
            final int statusCode = response.getStatusLine().getStatusCode();

            return statusCode >= 200 && statusCode < 300;

        } catch (IOException | URISyntaxException e) {
            log.error(ERROR_MESSAGE, e.getMessage());
        }

        return false;
    }

    /**
     * Gets {@link Request} by id
     *
     * @param requestId of request entity that must be returned
     * @return found request
     */
    public Request getRequestById(final long requestId) {
        final String requestByIdUri = getServerAddress() + String.format(REQUEST_BY_ID_URL, requestId);
        final HttpGet httpGet = new HttpGet(requestByIdUri);
        return getRequestSuppressExceptions(httpGet);
    }

    /**
     * Gets record about last made request
     * @return the last {@link Request}
     */
    public Request getLastRequest() {
        final String lastRequestUri = getServerAddress() + GET_LAST_REQUEST_URL;
        final HttpGet httpGet = new HttpGet(lastRequestUri);
        return getRequestSuppressExceptions(httpGet);
    }

    /**
     * Gets all existing {@link Request}
     *
     * @return {@link List<Request>}
     */
    public List<Request> getAllRequests() {
        final String getRequestsUri = getServerAddress() + GET_ALL_REQUESTS_URL;
        final HttpGet httpGet = new HttpGet(getRequestsUri);
        try {
            val httpResponse = getHttpClient().execute(httpGet);
            val jsonResponse = mapResponseToJson(httpResponse);
            return mapJsonToRequestsList(jsonResponse);
        } catch (IOException e) {
            log.error(ERROR_MESSAGE, e.getMessage());
        }
        return Collections.emptyList();
    }

    /**
     * Deletes request with the given id
     *
     * @param requestId of {@link Request} that must be deleted
     * @return true if request with the given id was deleted
     */
    public boolean deleteRequestById(final long requestId) {
        final String deleteRequestUri = getServerAddress() + String.format(REQUEST_BY_ID_URL, requestId);
        final HttpDelete httpDelete = new HttpDelete(deleteRequestUri);
        try {
            val response = httpClient.execute(httpDelete);
            return Boolean.parseBoolean(mapResponseToJson(response));
        } catch (IOException e) {
            log.error(ERROR_MESSAGE, e.getMessage());
        }
        return false;
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

    /**
     * Executes GET http request and returns {@link Request}
     *
     * @param httpGet - {@link HttpGet} to execute
     * @return obtained Request entity
     */
    private Request getRequestSuppressExceptions(final HttpGet httpGet) {
        try {
            val httpResponse = getHttpClient().execute(httpGet);
            val jsonResponse = mapResponseToJson(httpResponse);
            return mapJsonToObject(jsonResponse, Request.class);
        } catch (IOException e) {
            log.error(ERROR_MESSAGE, e.getMessage());
        }
        return null;
    }
}
