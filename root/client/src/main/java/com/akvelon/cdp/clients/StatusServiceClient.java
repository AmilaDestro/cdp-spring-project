package com.akvelon.cdp.clients;

import com.akvelon.cdp.entitieslibrary.RequestStatus;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;

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
        final String statusUri = getServerAddress() + STATUS_URL;
        final HttpGet httpGet = new HttpGet(statusUri);
        try {
            val response = getHttpClient().execute(httpGet);
            val jsonResponse = mapResponseToJson(response);
            return mapJsonToObject(jsonResponse, RequestStatus.class);
        } catch (IOException e) {
            log.error(ERROR_MESSAGE, e.getMessage());
        }
        return null;
    }

    /**
     * Deletes existing {@link RequestStatus}
     *
     * @return true if status record was deleted
     */
    public boolean deleteStatus() {
        final String statusUri = getServerAddress() + STATUS_URL;
        final HttpDelete httpDelete = new HttpDelete(statusUri);
        try {
            val response = getHttpClient().execute(httpDelete);
            return Boolean.parseBoolean(mapResponseToJson(response));
        } catch (IOException e) {
            log.error(ERROR_MESSAGE, e.getMessage());
        }
        return false;
    }
}
