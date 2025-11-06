package com.scalable.seatingservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatActionRequest {
    private String eventId;
    private String userId;
    private List<String> seatIds;
}
