package com.akvelon.cdp.executors;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.TimeUnit;

import com.akvelon.cdp.clients.RequestServiceClient;
import com.akvelon.cdp.clients.StatusServiceClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.eclipse.jetty.client.api.ContentResponse;

@Slf4j
@AllArgsConstructor
public class RedirectAndStatusUpdateExecutor extends AbstractActionExecutor {

    private final RequestServiceClient requestServiceClient;
    private final StatusServiceClient statusServiceClient;

    public void redirectToSpecifiedUrlAndWaitForStatusUpdate(final String url) {
        log.debug("Performing redirect to {} and waiting for status updates", url);
        final long currentRequestNumber = statusServiceClient.getStatus().getNumberOfRequest();
        executeTaskAndWaitForConditionSuccess(
                () -> requestServiceClient.redirectToSpecifiedUrlUpdateStatistic(url),
                () -> {
                    val status = statusServiceClient.getStatus();
                    return status.getLastRequestUrl().contains(url) &&
                            status.getNumberOfRequest() - currentRequestNumber == 1;
                });
    }

    public ContentResponse redirectToSpecifiedUrlAndWaitResponseIsSuccessful(final String url) {
        log.debug("Performing redirect to {} and waiting for response is OK", url);
        val response =  executeTaskAndWaitForResponse(
                () -> requestServiceClient.redirectToSpecifiedUrlUpdateStatistic(url), 20, SECONDS);
        return (ContentResponse) response;
    }
}
