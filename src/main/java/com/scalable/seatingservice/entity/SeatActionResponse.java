package com.scalable.seatingservice.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatActionResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Seats> seats;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String count;

    public SeatActionResponse(List<Seats> seatList) {
        this.seats = seatList;
    }
}