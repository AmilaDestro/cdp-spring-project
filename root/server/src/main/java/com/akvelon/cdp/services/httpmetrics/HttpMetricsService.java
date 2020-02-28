package com.akvelon.cdp.services.httpmetrics;

import com.akvelon.cdp.exceptions.NotFoundException;
import com.akvelon.cdp.utils.UrlPatternsUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.decimal4j.util.DoubleRounder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

import static java.lang.String.format;
import static org.apache.http.util.EntityUtils.toByteArray;

@Slf4j
@Component
public class HttpMetricsService extends AbstractHttpMetricsService {
    private final static DoubleRounder DECIMAL_NUMBERS_ROUNDER = new DoubleRounder(3);

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendGetRequestAndCollectMetrics(String url) throws NotFoundException {
        try {
            final HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader(HttpHeaders.CONTENT_TYPE, "text/html");

            log.debug("Executing GET request to URL {}", url);
            final long startTime = System.currentTimeMillis();
            final CloseableHttpResponse response = httpClient.execute(httpGet);
            final long endTime = System.currentTimeMillis() - startTime;

            requestDuration = DECIMAL_NUMBERS_ROUNDER.round( endTime / 1000D);
            log.debug("Request duration was {} seconds", requestDuration);

            receivedBytes = toByteArray(response.getEntity()).length;
            final double mBytes = DECIMAL_NUMBERS_ROUNDER.round(receivedBytes / 2048D);
            log.debug("Received bytes: {} MB", mBytes);

            dataTransferSpeed = calculateDataTransferSpeed(requestDuration, receivedBytes);
            log.debug("Speed: {} bytes/sec", dataTransferSpeed);
        } catch (final IOException e) {
            log.error("Exception {} was caught during execution of request to URL {}",
                    e.getMessage(), url);
            throw new NotFoundException(format("Web Page %s was not found", url));
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
     * @param sentBytes - size of received data in MB
     * @return calculated speed in MB/sec
     */
    private double calculateDataTransferSpeed(final double requestDurationInSeconds, final double sentBytes) {
        return DECIMAL_NUMBERS_ROUNDER.round(sentBytes / requestDurationInSeconds);
    }
}
