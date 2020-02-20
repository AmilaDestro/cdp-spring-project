package com.akvelon.cdp.services.status;

import com.akvelon.cdp.entities.Request;
import com.akvelon.cdp.entities.RequestStatus;
import org.springframework.stereotype.Component;

/**
 * Contains methods to manage {@link RequestStatus}.
 * Depends on created {@link Request} records.
 * {@link RequestStatus} record is single and updated each time when new request is created.
 */
@Component
public interface ApplicationStatusInterface {
    /**
     * Gets the only {@link RequestStatus} record or creates one if it doesn't exist
     *
     * @return {@link RequestStatus}
     */
    RequestStatus getOrCreateStatus();

    /**
     * Updates status record with the new {@link Request}
     *
     * @param statusId - id of {@link RequestStatus} entity that must be updated
     * @param createdRequest - newly created {@link Request} which is not present in status yet
     * @return updated status record with the last request
     */
    RequestStatus updateStatus(final long statusId, final Request createdRequest);

    /**
     * Deletes existing {@link RequestStatus}
     * @return true if the record was deleted
     */
    boolean deleteStatus();
}
