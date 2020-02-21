package com.akvelon.cdp.clients;

import com.akvelon.cdp.entitieslibrary.RequestStatus;
import lombok.val;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;

/**
 * Client for {@link RequestStatus}
 */
public class StatusServiceClient extends AbstractClient {
    private static final String STATUS_URL = "/status";

    /**
     * Gets current {@link RequestStatus}
     *
     * @return actual status with all requests statistics
     * @throws IOException if error occurs during GET request execution
     */
    public RequestStatus getStatus() throws IOException {
        final String statusUri = getServerAddress() + STATUS_URL;
        final HttpGet httpGet = new HttpGet(statusUri);

        val response = getHttpClient().execute(httpGet);
        val jsonResponse = mapResponseToJson(response);

        return mapJsonToObject(jsonResponse, RequestStatus.class);
    }

    /**
     * Deletes existing {@link RequestStatus}
     *
     * @return true if status record was deleted
     * @throws IOException if error occurs during DELETE request execution
     */
    public boolean deleteStatus() throws IOException {
        final String statusUri = getServerAddress() + STATUS_URL;
        final HttpDelete httpDelete = new HttpDelete(statusUri);
        val response = getHttpClient().execute(httpDelete);

        return Boolean.parseBoolean(mapResponseToJson(response));
    }
}
