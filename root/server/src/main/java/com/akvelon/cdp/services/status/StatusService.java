package com.akvelon.cdp.services.status;

import com.akvelon.cdp.entities.Request;
import com.akvelon.cdp.entities.RequestStatus;
import com.akvelon.cdp.persistence.RequestStatusRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Slf4j
public class StatusService implements ApplicationStatusInterface {

    @NonNull
    private RequestStatusRepository statusRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public RequestStatus getOrCreateStatus() {
        log.debug("Getting status record");
        final Optional<RequestStatus> statusSearch = statusRepository.getStatus();
        if (statusSearch.isEmpty()) {
            log.debug("Status record was not found - creating a new one");
            final RequestStatus statusToCreate = RequestStatus.builder()
                                                              .numberOfRequest(0)
                                                              .fullStatistic(new ArrayList<>())
                                                              .build();
            final RequestStatus createdStatus = statusRepository.save(statusToCreate);
            log.debug("Status record was created: {}", createdStatus);
            return createdStatus;
        }
        return statusSearch.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RequestStatus updateStatus(final long statusId, final Request createdRequest) {
        log.debug("Looking for status record with id {}", statusId);
        final RequestStatus existingStatus = statusRepository.findById(statusId).orElse(getOrCreateStatus());

        log.debug("Updating status record {} with new request: {}", statusId, createdRequest);
        existingStatus.setNumberOfRequest(existingStatus.getNumberOfRequest() + 1);
        existingStatus.setLastRequestUrl(createdRequest.getRequestUrl());
        existingStatus.setLastRequestTime(createdRequest.getRequestTime());
        existingStatus.setLastIpAddress(createdRequest.getIpAddress());

        return statusRepository.save(existingStatus);
    }

    @Override
    public boolean deleteStatus() {
        log.debug("Deleting status record from database.");
        final Optional<RequestStatus> statusToDelete = statusRepository.getStatus();
        if (statusToDelete.isPresent()) {
            statusRepository.deleteById(statusToDelete.get().getId());
            log.debug("Status was deleted.");
            return true;
        }
        log.debug("Status is not present. Nothing to delete.");
        return false;
    }
}
