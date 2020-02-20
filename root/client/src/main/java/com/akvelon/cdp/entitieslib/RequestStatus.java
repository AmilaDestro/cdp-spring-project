package com.akvelon.cdp.entitieslib;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
@ToString
public class RequestStatus {
    private long numberOfRequest;
    private String lastRequestUrl;
    private LocalDateTime lastRequestTime;
    private String lastIpAddress;
    private List<Request> fullStatistic;
}
