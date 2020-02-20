package com.akvelon.cdp.endpoints;

import com.akvelon.cdp.entities.RequestStatus;
import com.akvelon.cdp.services.status.ApplicationStatusInterface;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * REST controller for {@link RequestStatus} entity
 */
@RequiredArgsConstructor
@RestController
public class RequestStatusController {
    @NonNull
    private ApplicationStatusInterface statusService;

    /**
     * Displays information about history of GET HTTP requests to different sources
     *
     * @return {@link RequestStatus}
     */
    @RequestMapping(value = "/status", method = GET)
    public RequestStatus getStatus() {
        return statusService.getOrCreateStatus();
    }

    /**
     * Sends HTTP request to delete {@link RequestStatus}
     *
     * @return true if status was deleted
     */
    @RequestMapping(value = "/status", method = DELETE)
    public boolean deleteStatus() {
        return statusService.deleteStatus();
    }
}
