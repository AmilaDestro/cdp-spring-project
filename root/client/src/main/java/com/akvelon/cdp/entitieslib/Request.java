package com.akvelon.cdp.entitieslib;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@ToString
public class Request {
    private long id;
    private String requestUrl;
    private LocalDateTime requestTime;
    private String ipAddress;
    private double sentBytes;
    private double speed;
    private double requestDuration;
}
