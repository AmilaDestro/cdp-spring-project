package com.akvelon.cdp.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "status")
public class RequestStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private long id;

    @Column(name = "request_number")
    private long numberOfRequest;

    @Column(name = "last_request_url")
    private String lastRequestUrl;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "last_request_time")
    private LocalDateTime lastRequestTime;

    @Column(name = "last_ip_address")
    private String lastIpAddress;

    @OneToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "request",
    joinColumns = @JoinColumn(name = "request_status_id", updatable = false, insertable = false),
    inverseJoinColumns = @JoinColumn(name = "id"))
    private List<Request> fullStatistic;
}
