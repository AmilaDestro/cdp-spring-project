package com.akvelon.cdp.executors;

import com.akvelon.cdp.clients.RequestServiceClient;
import com.akvelon.cdp.clients.StatusServiceClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
@AllArgsConstructor
public class RedirectAndStatusUpdateExecutor extends AbstractActionExecutor {
    private final RequestServiceClient requestServiceClient;
    private final StatusServiceClient statusServiceClient;

    public void redirectToSpecifiedUrlAndWaitForStatusUpdate(final String url) {
        log.debug("Performing redirect to {} and waiting for status updates", url);
        final long currentRequestNumber = statusServiceClient.getStatus().getNumberOfRequest();
        executeAndWait(
                () -> requestServiceClient.redirectToSpecifiedUrlAndUpdateStatistic(url),
                () -> {
                    val status = statusServiceClient.getStatus();
                    return status.getLastRequestUrl().contains(url) && status.getNumberOfRequest() > currentRequestNumber;
                });
    }
}
