package com.scalable.seatingservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Seats {

    @Id
    @Column(name = "seat_id")
    private String seatId;
    @Column(name = "event_id")
    private String eventId;
    private String section;
    private String row;
    @Column(name = "seat_number")
    private String seatNumber;
    private String price;

    private String status; // AVAILABLE | RESERVED | ALLOCATED

    private String reservedBy;

    private LocalDateTime reservedAt;

    private LocalDateTime expiresAt;


    @JsonIgnore
    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }

    @JsonIgnore
    public boolean isAvailable() {
        String effectiveStatus = (this.status == null) ? "AVAILABLE" : this.status;
        if ("ALLOCATED".equalsIgnoreCase(effectiveStatus)) {
            return false;
        }
        return "AVAILABLE".equalsIgnoreCase(effectiveStatus) || isExpired();
    }

    @JsonIgnore
    public boolean isReserved() {
        String effectiveStatus = (this.status == null) ? "AVAILABLE" : this.status;
        return "RESERVED".equalsIgnoreCase(effectiveStatus) && !isExpired();
    }
}
