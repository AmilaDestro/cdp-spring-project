package com.akvelon.cdp.services.requests;

import com.akvelon.cdp.entities.Request;
import com.akvelon.cdp.entities.RequestStatus;
import com.akvelon.cdp.exceptions.NotFoundException;
import com.akvelon.cdp.exceptions.RequestNotFoundException;
import com.akvelon.cdp.persistence.RequestRepository;
import com.akvelon.cdp.services.status.StatusService;
import com.akvelon.cdp.services.httpmetrics.AbstractHttpMetricsService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for operations with {@link Request}
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class RequestService implements RequestWithMetrics {

    @NonNull
    private RequestRepository requestRepository;
    @NonNull
    private StatusService statusService;
    @NonNull
    private AbstractHttpMetricsService httpMetricsService;

    /**
     * {@inheritDoc}
     */
    @Override
    public Pair<String, Double> sendGetRequestAndReturnPage(final String url) throws NotFoundException {
        final String responseEntity = httpMetricsService.sendGetRequestAndCollectMetrics(url);

        createRequestRecordAndUpdateStatus(url,
                                           httpMetricsService.getIpAddress(url),
                                           httpMetricsService.getRequestDuration(),
                                           httpMetricsService.getReceivedBytes(),
                                           httpMetricsService.getDataTransferSpeed());
        return Pair.of(responseEntity, httpMetricsService.getReceivedBytes());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteInternalRequest(final long requestId) throws RequestNotFoundException {
        log.debug("Deleting request record with id {}", requestId);
        final Optional<Request> requestToDelete = requestRepository.findById(requestId);
        if (requestToDelete.isPresent()) {
            requestRepository.deleteById(requestId);
            log.debug("Request {} was deleted", requestId);
            return true;
        }
        log.debug("Request with id {} was not found and cannot be deleted.", requestId);
        throw new RequestNotFoundException(requestId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Request getInternalRequest(final long requestId) throws RequestNotFoundException {
        log.debug("Getting request record with id {}", requestId);
        return requestRepository.findById(requestId).orElseThrow(() -> new RequestNotFoundException(requestId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Request getLastCreatedInternalRequest() {
        log.debug("Getting last request error");
        return requestRepository.getLastRequest();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Request> getInternalRequests() {
        log.debug("Getting all requests records");
        return requestRepository.findAll();
    }

    /**
     * Creates a new {@link Request} with metrics and updates {@link RequestStatus}
     *
     * @param url             - URL to which request is executed
     * @param ipAddress       - IP address for specified URL
     * @param requestDuration - duration of request in seconds
     * @param bytes           - amount of received data during request in MBytes
     * @param speed           - speed of data transfer for the request in MB/sec
     */
    private Request createRequestRecordAndUpdateStatus(final String url,
                                                       final String ipAddress,
                                                       final Double requestDuration,
                                                       final Double bytes,
                                                       final Double speed) {
        final RequestStatus lastStatus = statusService.getOrCreateStatus();

        final Request requestToCreate = Request.builder()
                                               .requestUrl(url)
                                               .ipAddress(ipAddress)
                                               .requestStatus(lastStatus)
                                               .requestTime(LocalDateTime.now())
                                               .requestDuration(requestDuration)
                                               .sentBytes(bytes)
                                               .speed(speed)
                                               .build();
        log.debug("Creating new internal request record {}", requestToCreate);
        final Request createdRequest = requestRepository.save(requestToCreate);
        log.debug("Created internal request record {}", createdRequest);
        statusService.updateStatus(lastStatus.getId(), createdRequest);

        return createdRequest;
    }
}
