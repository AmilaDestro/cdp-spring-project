package com.akvelon.cdp.utils;

import static java.lang.String.format;

import java.util.Objects;
import java.util.Optional;

import com.akvelon.cdp.clients.RequestServiceClient;
import com.akvelon.cdp.entitieslibrary.Request;
import lombok.AllArgsConstructor;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpStatus;

/**
 * Contains useful methods to work with internal requests
 */
@AllArgsConstructor
public class RequestUtil {
    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";

    private RequestServiceClient requestServiceClient;

    /**
     * Gets {@link Request} entity by specified URL
     *
     * @param url to match internal request
     * @return found internal request
     */
    public Request getRequestByUrl(final String url) {
        return requestServiceClient.getAllRequests()
                                   .stream()
                                   .filter(request -> request.getRequestUrl().contains(url))
                                   .findFirst().orElse(null);
    }

    /**
     * Gets URL of {@link Request} without specification of protocol (http/https)
     *
     * @param visitedUrl - URL to be processed
     * @return {@link String} with URL without protocol
     */
    public static String getRequestUrlSkipProtocol(final String visitedUrl) {
        final String urlToReturn = visitedUrl.trim();
        if (visitedUrl.contains(HTTP)) {
            return urlToReturn.substring(HTTP.length());
        }

        if (visitedUrl.contains(HTTPS)) {
            return urlToReturn.substring(HTTPS.length());
        }

        return urlToReturn;
    }

    /**
     * Gets {@link Request} by specified id and suppresses exceptions
     *
     * @param requestId of internal request to get
     * @return {@link Optional<Request>}
     */
    public Optional<Request> getRequestByIdSuppressExceptions(final long requestId) {
        final ContentResponse response = requestServiceClient.getRequestById(requestId);
        if (response.getStatus() == HttpStatus.OK_200 && Objects.nonNull(response.getContentAsString())) {
            return Optional.of(requestServiceClient.mapJsonToObject(response.getContentAsString(), Request.class));
        }
        return Optional.empty();
    }

    /**
     * Gets {@link Request} by specified id
     *
     * @param requestId of internal request to get
     * @return found {@link Request}
     */
    public Request getRequestById(final long requestId) {
        return getRequestByIdSuppressExceptions(requestId)
                .orElseThrow(() -> new AssertionError(format("Request with id %s was not found", requestId)));
    }
}
