package com.scalable.seatingservice.service;

import com.scalable.seatingservice.config.AppConstants;
import com.scalable.seatingservice.entity.SeatActionRequest;
import com.scalable.seatingservice.entity.SeatActionResponse;
import com.scalable.seatingservice.entity.Seats;
import com.scalable.seatingservice.entity.SeatsApiResponse;
import com.scalable.seatingservice.exception.BaseException;
import com.scalable.seatingservice.repository.SeatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SeatsServiceImpl implements SeatsService{

    @Autowired
    SeatsRepository seatsRepository;

    @Autowired
    AppConstants appConstants;

    @Override
    public SeatsApiResponse<SeatActionResponse> findSeatsByEventId(String eventId) throws BaseException {
        List<Seats> seatsList = seatsRepository.findByEventId(eventId);
        if(seatsList.isEmpty()){
            throw new BaseException(appConstants.getErrorCodes().getNotFoundCode(),appConstants.getErrorCodes().getNotFoundMessage());
        }
        else {
            SeatActionResponse response = new SeatActionResponse();
            response.setSeats(seatsList);
            return new SeatsApiResponse<>(appConstants.getErrorCodes().getOkMessage(),appConstants.getErrorCodes().getOkCode(),appConstants.getErrorCodes().getOkMessage(),response);
        }

    }

    @Override
    public SeatsApiResponse<SeatActionResponse> reserveSeats(SeatActionRequest request) throws BaseException {
        List<Seats> seatsList = seatsRepository.findByEventIdAndSeatIdIn(request.getEventId(), request.getSeatIds());
        if(seatsList.isEmpty()){
            throw new BaseException(appConstants.getErrorCodes().getNotFoundCode(),appConstants.getErrorCodes().getNotFoundMessage());
        }
        List<String> unavailableSeats = new ArrayList<>();
        for (Seats seat : seatsList) {
            if (!seat.isAvailable()) {
                unavailableSeats.add(seat.getSeatId());
            }
        }
        if (!unavailableSeats.isEmpty()) {
            throw new BaseException(
                    appConstants.getErrorCodes().getSeatUnavailableCode(),
                    appConstants.getErrorCodes().getSeatUnavailableMessage() + String.join(",", unavailableSeats)
            );
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusMinutes(5);

        for (Seats seat : seatsList) {
            seat.setStatus("RESERVED");
            seat.setReservedBy(request.getUserId());
            seat.setReservedAt(now);
            seat.setExpiresAt(expiry);
        }

        List<Seats> responseList = seatsRepository.saveAll(seatsList);

        SeatsApiResponse<SeatActionResponse> response = new SeatsApiResponse<>();
        response.setCode(appConstants.getErrorCodes().getOkCode());
        response.setStatus(appConstants.getErrorCodes().getReservedStatus());
        response.setMessage(appConstants.getErrorCodes().getReservedStatus());
        response.setData( new SeatActionResponse(responseList));

        return response;
    }

    @Override
    public SeatsApiResponse<SeatActionResponse> allocateSeats(SeatActionRequest request) throws BaseException {
        List<Seats> seatsList = seatsRepository.findByEventIdAndSeatIdIn(request.getEventId(), request.getSeatIds());
        if(seatsList.isEmpty()){
            throw new BaseException(appConstants.getErrorCodes().getNotFoundCode(),appConstants.getErrorCodes().getNotFoundMessage());
        }
        List<String> unavailableSeats = new ArrayList<>();
        for (Seats seat : seatsList) {
            if (!seat.isReserved()) {
                unavailableSeats.add(seat.getSeatId());
            }

            if (!seat.getReservedBy().equals(request.getUserId())) {
                unavailableSeats.add(seat.getSeatId());
            }

        }
        if (!unavailableSeats.isEmpty()) {
            throw new BaseException(
                    appConstants.getErrorCodes().getSeatUnavailableCode(),
                    appConstants.getErrorCodes().getSeatUnavailableMessage() + String.join(",", unavailableSeats)
            );
        }


        for (Seats seat : seatsList) {
            seat.setStatus(appConstants.getErrorCodes().getAllocatedStatus());
            seat.setReservedBy(request.getUserId());
            seat.setReservedAt(null);
            seat.setExpiresAt(null);
        }

        List<Seats> responseList = seatsRepository.saveAll(seatsList);

        SeatsApiResponse<SeatActionResponse> response = new SeatsApiResponse<>();
        response.setCode(appConstants.getErrorCodes().getOkCode());
        response.setStatus(appConstants.getErrorCodes().getAllocatedStatus());
        response.setMessage(appConstants.getErrorCodes().getAllocatedStatus());
        response.setData( new SeatActionResponse(responseList));

        return response;
    }

    @Override
    public SeatsApiResponse<SeatActionResponse> releaseSeats(SeatActionRequest request) throws BaseException {
        List<Seats> seatsList = seatsRepository.findByEventIdAndSeatIdIn(request.getEventId(), request.getSeatIds());
        if(seatsList.isEmpty()){
            throw new BaseException(appConstants.getErrorCodes().getNotFoundCode(),appConstants.getErrorCodes().getNotFoundMessage());
        }
        List<String> unavailableSeats = new ArrayList<>();
        for (Seats seat : seatsList) {
            if (!seat.isReserved()) {
                unavailableSeats.add(seat.getSeatId());
            }

            if (!seat.getReservedBy().equals(request.getUserId())) {
                unavailableSeats.add(seat.getSeatId());
            }
        }
        if (!unavailableSeats.isEmpty()) {
            throw new BaseException(
                    appConstants.getErrorCodes().getSeatUnavailableCode(),
                    appConstants.getErrorCodes().getSeatUnavailableMessage() + String.join(",", unavailableSeats)
            );
        }


        for (Seats seat : seatsList) {
            seat.setStatus(appConstants.getErrorCodes().getAvailableStatus());
            seat.setReservedBy(null);
            seat.setReservedAt(null);
            seat.setExpiresAt(null);
        }

        List<Seats> responseList = seatsRepository.saveAll(seatsList);

        SeatsApiResponse<SeatActionResponse> response = new SeatsApiResponse<>();
        response.setCode(appConstants.getErrorCodes().getOkCode());
        response.setStatus(appConstants.getErrorCodes().getAvailableStatus());
        response.setMessage(appConstants.getErrorCodes().getAvailableStatus());
        response.setData( new SeatActionResponse(responseList));

        return response;
    }

    @Override
    public SeatsApiResponse<SeatActionResponse> getSeatCountByEvent(String eventId) {
       long count = seatsRepository.countByeventId(eventId);
       SeatsApiResponse<SeatActionResponse> response = new SeatsApiResponse<SeatActionResponse>();
       response.setData(new SeatActionResponse(null,String.valueOf(count)));
       response.setMessage("Success");
       response.setCode("200");
       response.setStatus("Success");
       return response;
    }
}
