package com.akvelon.cdp.services.httpmetrics;

import com.akvelon.cdp.exceptions.WebPageNotFoundException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.stereotype.Component;

/**
 * Contains methods to collect some metrics about HTTP requests
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
@Component
@Slf4j
public abstract class AbstractHttpMetricsService {

    protected final HttpClient httpClient;
    protected double receivedBytes;
    protected double requestDuration;
    protected double dataTransferSpeed;

    public AbstractHttpMetricsService() {
        final SslContextFactory sslContextFactory = new SslContextFactory.Client();
        httpClient = new HttpClient(sslContextFactory);
        httpClient.setConnectTimeout(10000);
    }

    /**
     * Starts {@link HttpClient}
     */
    protected void startHttpClient() {
        try {
            httpClient.start();
        } catch (final Exception e) {
            log.error("Http client start failed");
        }
    }

    /**
     * Stops {@link HttpClient}
     */
    protected void stopHttpClient() {
        try {
            httpClient.stop();
        } catch (final Exception e) {
            log.error("Http client stop failed");
        }
    }

    /**
     * Executes GET HTTP request to specified URL and collects metrics:
     * request duration in seconds, received data in MBytes, data transfer speed in MB/sec
     *
     * @param url - URL to which the request is executed
     */
    public abstract String sendGetRequestAndCollectMetrics(final String url);

    /**
     * Gets info about IP address for specified URL
     *
     * @param url - URL that is being checked
     * @return found IP address as String
     */
    public abstract String getIpAddress(final String url);
}
