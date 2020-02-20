package com.akvelon.cdp.persistence;

import com.akvelon.cdp.entities.Request;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository of {@link Request} entities
 */

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    @Query(value = "select * from request order by request.request_time desc limit 1", nativeQuery = true)
    Request getLastRequest();
}
