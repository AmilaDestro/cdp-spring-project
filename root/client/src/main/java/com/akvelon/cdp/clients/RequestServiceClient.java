package com.akvelon.cdp.clients;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.eclipse.jetty.http.HttpMethod.DELETE;
import static org.eclipse.jetty.http.HttpMethod.GET;
import static org.eclipse.jetty.http.HttpStatus.OK_200;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.akvelon.cdp.entitieslibrary.Request;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.eclipse.jetty.client.api.ContentResponse;

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
    public ContentResponse redirectToSpecifiedUrlUpdateStatistic(final String url) {
        final String query = format("?url=%s", url);
        final String redirectUri = getServerAddress() + REDIRECT_URL;
        val redirectRequest = httpClient.newRequest(redirectUri + query)
                                        .timeout(20, SECONDS)
                                        .method(GET);
        return executeHttpRequest(redirectRequest);
    }

    /**
     * Gets {@link Request} by id
     *
     * @param requestId of request entity that must be returned
     * @return found request
     */
    public Request getRequestById(final long requestId) {
        final String requestByIdUri = getServerAddress() + format(REQUEST_BY_ID_URL, requestId);
        val getInternalRequest = httpClient.newRequest(requestByIdUri)
                                           .method(GET)
                                           .timeout(5, SECONDS);
        val contentResponse = executeHttpRequest(getInternalRequest);
        return mapJsonToObject(contentResponse.getContentAsString(), Request.class);
    }

    /**
     * Gets record about last made request
     *
     * @return the last {@link Request}
     */
    public Request getLastRequest() {
        final String lastRequestUri = getServerAddress() + GET_LAST_REQUEST_URL;
        val getInternalRequest = httpClient.newRequest(lastRequestUri)
                                           .method(GET)
                                           .timeout(5, SECONDS);
        val contentResponse = executeHttpRequest(getInternalRequest);
        return mapJsonToObject(contentResponse.getContentAsString(), Request.class);
    }

    /**
     * Gets all existing {@link Request}
     *
     * @return {@link List<Request>}
     */
    public List<Request> getAllRequests() {
        final String getRequestsUri = getServerAddress() + GET_ALL_REQUESTS_URL;
        val getInternalRequests = httpClient.newRequest(getRequestsUri)
                                            .method(GET)
                                            .timeout(10, SECONDS);
        val contentResponse = executeHttpRequest(getInternalRequests);
        return mapJsonToRequestsList(contentResponse.getContentAsString());
    }

    /**
     * Deletes request with the given id
     *
     * @param requestId of {@link Request} that must be deleted
     * @return true if request with the given id was deleted
     */
    public boolean deleteRequestById(final long requestId) {
        final String deleteRequestUri = getServerAddress() + format(REQUEST_BY_ID_URL, requestId);
        val deleteRequest = httpClient.newRequest(deleteRequestUri)
                                      .method(DELETE)
                                      .timeout(5, SECONDS);
        val contentResponse = executeHttpRequest(deleteRequest);
        return contentResponse.getStatus() == OK_200 &&
                Boolean.parseBoolean(contentResponse.getContentAsString());
    }

    /**
     * Maps json array to list of requests
     *
     * @param json to process
     * @return {@link List<Request>}
     */
    private List<Request> mapJsonToRequestsList(final String json) {
        try {
            final Request[] requestsArray = objectMapper.readValue(json, Request[].class);
            return Arrays.asList(requestsArray);
        } catch (final JsonProcessingException e) {
            log.error("Exception was thrown while mapping JSON {} to List<Request>:\n{}", json, e.getMessage());
        }
        return Collections.emptyList();
    }
}
