package com.akvelon.cdp.services.httpmetrics;

import com.akvelon.cdp.exceptions.NotFoundException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Component;

/**
 * Contains methods to collect some metrics about HTTP requests
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
@Component
public abstract class AbstractHttpMetricsService {
    protected final CloseableHttpClient httpClient;
    protected double receivedBytes;
    protected double requestDuration;
    protected double dataTransferSpeed;

    public AbstractHttpMetricsService() {
        this.httpClient = HttpClients.createDefault();
    }

    /**
     * Executes GET HTTP request to specified URL and collects metrics:
     * request duration in seconds, received data in MBytes, data transfer speed in MB/sec
     *
     * @param url - URL to which the request is executed
     */
    public abstract void sendGetRequestAndCollectMetrics(final String url) throws NotFoundException;

    /**
     * Gets info about IP address for specified URL
     *
     * @param url - URL that is being checked
     * @return found IP address as String
     */
    public abstract String getIpAddress(final String url);
}
