package com.akvelon.cdp.persistence;

import com.akvelon.cdp.entities.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository of {@link RequestStatus} entity
 */

@Repository
public interface RequestStatusRepository extends JpaRepository<RequestStatus, Long> {

    default Optional<RequestStatus> getStatus() {
        final List<RequestStatus> allStatusRecords = findAll();
        return allStatusRecords.isEmpty() ? Optional.empty() : Optional.of(allStatusRecords.get(0));
    }
}
