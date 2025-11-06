package com.scalable.seatingservice.controller;


import com.scalable.seatingservice.entity.SeatActionRequest;
import com.scalable.seatingservice.exception.BaseException;
import com.scalable.seatingservice.service.SeatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/seats")
public class SeatsController {

    @Autowired
    SeatsService service;

    @GetMapping
    public ResponseEntity<?> getSeatsByEventId(@RequestParam("eventId") String eventId,
    @RequestHeader( value = "trackingHeader" ,required = true) String trackingHeader) throws BaseException {
        return ResponseEntity.ok(service.findSeatsByEventId(eventId));
    }


    @GetMapping("/availability/{eventId}" )
    public ResponseEntity<?> getSeatCountsForEvent(@PathVariable String eventId,
                                                   @RequestHeader( value = "trackingHeader" ,required = true) String trackingHeader){
        return ResponseEntity.ok(service.getSeatCountByEvent(eventId));
    }

    @PostMapping("/reserve")
    public ResponseEntity<?> reserveSeats(@RequestHeader( value = "trackingHeader" ,required = true) String trackingHeader ,
                                          @RequestBody SeatActionRequest seatActionRequest) throws BaseException {
        return ResponseEntity.ok(service.reserveSeats(seatActionRequest));
    }

    @PostMapping("/allocate")
    public ResponseEntity<?> allocateSeats(@RequestHeader( value = "trackingHeader" ,required = true) String trackingHeader ,
                                          @RequestBody SeatActionRequest seatActionRequest) throws BaseException {
        return ResponseEntity.ok(service.allocateSeats(seatActionRequest));
    }

    @PostMapping("/release")
    public ResponseEntity<?> releaseSeats(@RequestHeader( value = "trackingHeader" ,required = true) String trackingHeader ,
                                           @RequestBody SeatActionRequest seatActionRequest) throws BaseException {
        return ResponseEntity.ok(service.releaseSeats(seatActionRequest));
    }
}
