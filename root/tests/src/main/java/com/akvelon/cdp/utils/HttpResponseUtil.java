package com.akvelon.cdp.utils;

import static java.lang.String.format;

import com.akvelon.cdp.clients.AbstractClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpStatus;

@AllArgsConstructor
@Slf4j
public class HttpResponseUtil {

    private AbstractClient abstractClient;

    public static boolean isSuccessfulResponse(final ContentResponse response) {
        return getStatusCode(response) == HttpStatus.OK_200;
    }

    public static int getStatusCode(final ContentResponse response) {
        return response.getStatus();
    }

    public String getResponseEntityInJson(final ContentResponse response) {
        return response.getContentAsString();
    }
}
