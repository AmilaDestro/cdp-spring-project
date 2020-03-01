package com.akvelon.cdp.executors;

import java.util.List;
import java.util.stream.Collectors;

import com.akvelon.cdp.clients.RequestServiceClient;
import com.akvelon.cdp.clients.StatusServiceClient;
import com.akvelon.cdp.entitieslibrary.Request;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Contains methods to work with updates of {@link Request} entity
 */

@Slf4j
@AllArgsConstructor
public class RequestActionExecutor extends AbstractActionExecutor {

    private final RequestServiceClient requestServiceClient;
    private final StatusServiceClient statusServiceClient;

    /**
     * Deletes {@link Request} by specified id and waits for updates in database
     *
     * @param requestId of internal request to delete
     */
    public void deleteRequestAndWaitItIsAbsentInDb(final long requestId) {
        log.debug("Deleting Request {} and waiting for it to be absent in database", requestId);
        executeTaskAndWaitForConditionSuccess(
                () -> requestServiceClient.deleteRequestById(requestId),
                () -> !getRequestIds().contains(requestId) && !getStatusFullStatisticRequestIds().contains(requestId));
    }

    private List<Long> getRequestIds() {
        return requestServiceClient.getAllRequests()
                                   .stream()
                                   .map(Request::getId)
                                   .collect(Collectors.toList());
    }

    private List<Long> getStatusFullStatisticRequestIds() {
        return statusServiceClient.getStatus().getFullStatistic()
                                  .stream()
                                  .map(Request::getId)
                                  .collect(Collectors.toList());
    }
}
