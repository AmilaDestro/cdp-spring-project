package com.akvelon.cdp.clients;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.eclipse.jetty.http.HttpMethod.DELETE;
import static org.eclipse.jetty.http.HttpMethod.GET;

import com.akvelon.cdp.entitieslibrary.RequestStatus;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.eclipse.jetty.http.HttpStatus;

/**
 * Client for {@link RequestStatus}
 */
@Slf4j
public class StatusServiceClient extends AbstractClient {

    private static final String STATUS_URL = "/status";

    /**
     * Gets current {@link RequestStatus}
     *
     * @return actual status with all requests statistics
     */
    public RequestStatus getStatus() {
        final String statusUrl = getServerAddress() + STATUS_URL;
        val getStatus = httpClient.newRequest(statusUrl)
                                  .method(GET)
                                  .timeout(5, SECONDS);
        val contentResponse = executeHttpRequest(getStatus);
        return mapJsonToObject(contentResponse.getContentAsString(), RequestStatus.class);
    }

    /**
     * Deletes existing {@link RequestStatus}
     *
     * @return true if status record was deleted
     */
    public boolean deleteStatus() {
        final String statusUrl = getServerAddress() + STATUS_URL;
        val deleteStatus = httpClient.newRequest(statusUrl)
                                     .method(DELETE)
                                     .timeout(5, SECONDS);
        val contentResponse = executeHttpRequest(deleteStatus);
        return contentResponse.getStatus() == HttpStatus.OK_200 &&
                Boolean.parseBoolean(contentResponse.getContentAsString());
    }
}
