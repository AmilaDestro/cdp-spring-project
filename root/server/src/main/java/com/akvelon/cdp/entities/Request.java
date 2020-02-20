package com.akvelon.cdp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "request")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "request_url")
    private String requestUrl;

    @Column(name = "request_time")
    private LocalDateTime requestTime;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "sent_bytes")
    private double sentBytes;

    @Column(name = "speed")
    private double speed;

    @Column(name = "request_duration")
    private double requestDuration;

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne
    @JoinColumn(name = "request_status_id")
    private RequestStatus requestStatus;
}
