package com.akvelon.cdp.services.requests;

import com.akvelon.cdp.entities.Request;
import com.akvelon.cdp.exceptions.NotFoundException;
import com.akvelon.cdp.exceptions.RequestNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Interface which contains methods for creation/deletion of records
 * about HTTP requests to different URLs with some metrics
 */
@Component
public interface RequestWithMetrics {

    /**
     * Creates a new {@link Request} for specified URL
     *
     * @param url - URL to which GET HTTP request is executed and for which metrics are collected
     * @return created {@link Request}
     */
    Request createRequest(final String url) throws NotFoundException;

    /**
     * Deletes {@link Request} with the gived id
     *
     * @param requestId of request record that must be deleted
     * @return deleted {@link Request}
     */
    boolean deleteRequest(final long requestId) throws RequestNotFoundException;

    /**
     * Gets existing {@link Request} with the given id
     *
     * @param requestId of request record that must be returned
     * @return found {@link Request}
     */
    Request getRequest(final long requestId) throws RequestNotFoundException;

    /**
     * Gets last created request record
     * @return last created {@link Request} entity
     */
    Request getLastCreatedRequest();

    /**
     * Gets all records about created {@link Request}
     * @return {@link List<Request>}
     */
    List<Request> getRequests();
}
