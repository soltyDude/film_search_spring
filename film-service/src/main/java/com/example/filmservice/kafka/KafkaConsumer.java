package com.example.filmservice.kafka;

import com.example.filmservice.service.FilmService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final FilmService filmService;

    @KafkaListener(topics = "film-rating-update", groupId = "film-service-group")
    public void listen(String message) {
        try {
            System.out.println("RAW MESSAGE = " +
                    "aaaaaaaaaaaaaaaaaaaaaaaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "" +
                    "" +
                    "" +
                    "" +
                    "" +
                    "" +
                    "" +
                    "" +
                    "" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "" +
                    "" +
                    "" +
                    "" + message);
            JsonObject json = JsonParser.parseString(message).getAsJsonObject();

            Long filmId = json.get("filmId").getAsLong();
            double rating = json.get("rating").getAsDouble();
            int count = json.get("count").getAsInt();

            filmService.updateRatingAndCount(filmId, rating, count);

        } catch (Exception e) {
            System.err.println("❌ KafkaConsumer failed to parse message: " + message);
            e.printStackTrace(); // чтобы не падало
        }
    }



}
