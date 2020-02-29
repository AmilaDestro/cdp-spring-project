package com.akvelon.cdp.services.httpmetrics;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.akvelon.cdp.exceptions.NotFoundException;
import com.akvelon.cdp.exceptions.WebPageNotFoundException;
import com.akvelon.cdp.utils.UrlPatternsUtil;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.decimal4j.util.DoubleRounder;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HttpMetricsService extends AbstractHttpMetricsService {

    private final static DoubleRounder DECIMAL_NUMBERS_ROUNDER = new DoubleRounder(3);
    private static final String LOCALHOST = "localhost";
    private final static Map<String, String> LOCAL_ADDRESS = ImmutableMap.of("localhost", "127.0.0.1");

    /**
     * {@inheritDoc}
     */
    @Override
    public String sendGetRequestAndCollectMetrics(String url) throws NotFoundException {
        startHttpClient();
        final Request getRequest = httpClient.newRequest(url)
                                             .method(HttpMethod.GET)
                                             .timeout(20, TimeUnit.SECONDS);
        try {
            log.debug("Executing GET request to URL {}", url);
            final long startTime = System.currentTimeMillis();
            final ContentResponse response = getRequest.send();
            final long endTime = System.currentTimeMillis() - startTime;

            if (response.getStatus() != HttpStatus.OK_200) {
                throw new WebPageNotFoundException(url);
            }

            requestDuration = DECIMAL_NUMBERS_ROUNDER.round(endTime / 1000D);
            log.debug("Request duration was {} seconds", requestDuration);

            receivedBytes = response.getContent().length;
            final double mBytes = DECIMAL_NUMBERS_ROUNDER.round(receivedBytes / 2048D);
            log.debug("Received bytes: {} MB", mBytes);

            dataTransferSpeed = calculateDataTransferSpeed(requestDuration, receivedBytes);
            log.debug("Speed: {} bytes/sec", dataTransferSpeed);

            return response.getContentAsString();
        } catch (TimeoutException | ExecutionException | InterruptedException e) {
            log.error("Exception {} was thrown during execution of request to URL {}", e.getMessage(), url);
            throw new RuntimeException();
        } finally {
            stopHttpClient();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIpAddress(String url) {
        return getIpAddressSuppressExceptions(url).orElse("UNKNOWN");
    }

    /**
     * Gets info about IP address for specified URL and suppresses exceptions
     *
     * @param url - URL that is being checked
     * @return {@link Optional<String>} with found IP address
     */
    private Optional<String> getIpAddressSuppressExceptions(String url) {
        log.debug("Getting IP address for URL {}", url);
        try {
            final String hostName = UrlPatternsUtil.getDomainNameHostSkipURI(url);
            if (hostName.startsWith(LOCALHOST)) {
                return Optional.of(LOCAL_ADDRESS.get(LOCALHOST));
            }
            final InetAddress inetAddress = InetAddress.getByName(hostName);
            return Optional.ofNullable(inetAddress.getHostAddress());
        } catch (final UnknownHostException e) {
            log.error("Exception was thrown when getting IP address of source {}:\n{}", url, e.getMessage());
        }
        log.debug("IP address for URL {} was not found", url);
        return Optional.empty();
    }

    /**
     * Calculates speed ot which data of specified MB size was transferred during request duration
     *
     * @param requestDurationInSeconds - duration of HTTP request in seconds
     * @param sentBytes                - size of received data in bytes
     * @return calculated speed in bytes/sec
     */
    private double calculateDataTransferSpeed(final double requestDurationInSeconds, final double sentBytes) {
        return DECIMAL_NUMBERS_ROUNDER.round(sentBytes / requestDurationInSeconds);
    }
}
