package com.scalable.seatingservice.service;

import com.scalable.seatingservice.entity.SeatActionRequest;
import com.scalable.seatingservice.entity.SeatActionResponse;
import com.scalable.seatingservice.entity.SeatsApiResponse;
import com.scalable.seatingservice.exception.BaseException;

public interface SeatsService {

    SeatsApiResponse<SeatActionResponse> findSeatsByEventId(String eventId) throws BaseException;
    SeatsApiResponse<SeatActionResponse> reserveSeats(SeatActionRequest request) throws BaseException;
    SeatsApiResponse<SeatActionResponse> allocateSeats(SeatActionRequest request) throws BaseException;
    SeatsApiResponse<SeatActionResponse> releaseSeats(SeatActionRequest request) throws BaseException;
    SeatsApiResponse<SeatActionResponse> getSeatCountByEvent(String eventId);



}
