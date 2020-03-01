package com.akvelon.cdp.executors;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.Objects;

import com.akvelon.cdp.clients.RequestServiceClient;
import com.akvelon.cdp.clients.StatusServiceClient;
import com.akvelon.cdp.entitieslibrary.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.eclipse.jetty.client.api.ContentResponse;

/**
 * Contains methods to work with combined updates in Request Service and Status Service
 */

@Slf4j
@AllArgsConstructor
public class RedirectAndStatusUpdateExecutor extends AbstractActionExecutor {

    private final RequestServiceClient requestServiceClient;
    private final StatusServiceClient statusServiceClient;

    /**
     * Performs redirect to specified URL and waits until new internal request record is created and
     * {@link RequestStatus} is updated
     *
     * @param url - URL to redirect to
     */
    public void redirectToSpecifiedUrlAndWaitForStatusUpdate(final String url) {
        log.debug("Performing redirect to {} and waiting for status updates", url);
        final long currentRequestNumber = statusServiceClient.getStatus().getNumberOfRequest();
        executeTaskAndWaitForConditionSuccess(
                () -> requestServiceClient.redirectToSpecifiedUrlUpdateStatistic(url),
                () -> {
                    val status = statusServiceClient.getStatus();
                    return Objects.nonNull(status) && Objects.nonNull(status.getLastRequestUrl())
                            && status.getLastRequestUrl().contains(url)
                            && status.getNumberOfRequest() - currentRequestNumber == 1;
                });
    }

    /**
     * Performs redirect to specified URL and waits until response is received
     *
     * @param url - URL to redirect to
     * @return {@link ContentResponse}
     */
    public ContentResponse redirectToSpecifiedUrlAndWaitResponseIsSuccessful(final String url) {
        log.debug("Performing redirect to {} and waiting for response is OK", url);
        val response = executeTaskAndWaitForResponse(
                () -> requestServiceClient.redirectToSpecifiedUrlUpdateStatistic(url), 20, SECONDS);
        return (ContentResponse) response;
    }
}
