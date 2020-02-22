package com.akvelon.cdp.utils;

import com.akvelon.cdp.clients.RequestServiceClient;
import com.akvelon.cdp.entitieslibrary.Request;
import lombok.AllArgsConstructor;

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
}
