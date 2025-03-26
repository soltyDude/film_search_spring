package com.example.reviewservice.kafka;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendRatingUpdate(Long filmId, double rating, int count) {
        JsonObject message = new JsonObject();
        message.addProperty("filmId", filmId);
        message.addProperty("rating", rating);
        message.addProperty("count", count);

        kafkaTemplate.send("film-rating-update", message.toString());
    }
}
