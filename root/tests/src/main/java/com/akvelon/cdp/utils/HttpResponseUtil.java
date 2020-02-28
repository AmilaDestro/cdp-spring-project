package com.akvelon.cdp.utils;

import com.akvelon.cdp.clients.AbstractClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Optional;

@AllArgsConstructor
@Slf4j
public class HttpResponseUtil {
    private AbstractClient abstractClient;

    public static boolean isSuccessfulResponse(final HttpResponse response) {
        int code = getStatusCode(response);
        return code >= 200 && code < 300;
    }

    public static int getStatusCode(final HttpResponse response) {
        return response.getStatusLine().getStatusCode();
    }

    public String getResponseEntityInJson(final HttpResponse response) {
        return getResponseEntityInJsonSuppressException(response).orElseThrow();
    }

    public String getExceptionInJsonResponse(final HttpResponse response) {
        if (getStatusCode(response) == 500) {
            return getResponseEntityInJson(response);
        }
        throw new AssertionError("HTTP response has OK status");
    }

    private Optional<String> getResponseEntityInJsonSuppressException(final HttpResponse response) {
        try {
            val jsonEntity = EntityUtils.toString(response.getEntity());
            return Optional.of(jsonEntity);
        } catch (final IOException e) {
            log.error("Entity couldn't be converted to JSON: {}", e.getMessage());
        }
        return Optional.empty();
    }
}
