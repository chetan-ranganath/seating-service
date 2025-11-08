package com.scalable.seatingservice.service;

import com.scalable.seatingservice.entity.Seats;
import com.scalable.seatingservice.repository.SeatsRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SeedService {

    private final SeatsRepository repository;

    public SeedService(SeatsRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void loadSeatsFromCsv() {
        if (repository.count() > 0) return;
        List<Seats> seatsToSave = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource("etsr_seats.csv").getInputStream()))) {

            reader.lines().skip(1).forEach(line -> {
                String[] row = line.split(",");

                Seats seat = new Seats();
                seat.setSeatId(row[0]);
                seat.setEventId(row[1]);
                seat.setSection(row[2]);
                seat.setRow(row[3]);
                seat.setSeatNumber(row[4]);
                seat.setPrice(row[5]);
                seatsToSave.add(seat);

            });
            repository.saveAll(seatsToSave);
            log.info("Seeded " + seatsToSave.size() + " seats from CSV successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
