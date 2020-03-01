package com.akvelon.cdp.utils;

import static java.lang.String.format;

import java.util.Objects;
import java.util.Optional;

import com.akvelon.cdp.clients.RequestServiceClient;
import com.akvelon.cdp.entitieslibrary.Request;
import lombok.AllArgsConstructor;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpStatus;

@AllArgsConstructor
public class RequestUtil {

    private RequestServiceClient requestServiceClient;

    public Request getRequestByUrl(final String url) {
        return requestServiceClient.getAllRequests()
                                   .stream()
                                   .filter(request -> request.getRequestUrl().contains(url))
                                   .findFirst().orElse(null);
    }

    public static String getLastRequestUrlSkipProtocol(final String visitedUrl) {
        final String http = "http://";
        final String https = "https://";
        if (visitedUrl.contains(http)) {
            return visitedUrl.substring(http.length());
        }

        if (visitedUrl.contains(https)) {
            return visitedUrl.substring(https.length());
        }

        return visitedUrl;
    }

    public Optional<Request> getRequestByIdSuppressExceptions(final long requestId) {
        final ContentResponse response = requestServiceClient.getRequestById(requestId);
        if (response.getStatus() == HttpStatus.OK_200 && Objects.nonNull(response.getContentAsString())) {
            return Optional.of(requestServiceClient.mapJsonToObject(response.getContentAsString(), Request.class));
        }
        return Optional.empty();
    }

    public Request getRequestById(final long requestId) {
        return getRequestByIdSuppressExceptions(requestId)
                .orElseThrow(() -> new AssertionError(format("Request with id %s was not found", requestId)));
    }
}
