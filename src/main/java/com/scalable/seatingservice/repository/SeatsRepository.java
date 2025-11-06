package com.scalable.seatingservice.repository;

import com.scalable.seatingservice.entity.Seats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatsRepository extends JpaRepository<Seats,String> {
    List<Seats> findByEventId(String eventId);
    List<Seats> findByEventIdAndSeatIdIn(String eventId , List<String> seatIds);
    long countByeventId(String eventId);
}
