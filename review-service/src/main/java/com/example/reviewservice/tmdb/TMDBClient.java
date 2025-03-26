package com.example.reviewservice.tmdb;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class TMDBClient {

    @Value("${tmdb.api_key}")
    private String apiKey;

    @Value("${tmdb.base_url}")
    private String baseUrl;

    public JsonObject fetchSimilarMovies(int apiId) throws Exception {
        String urlStr = baseUrl + "/movie/" + apiId + "/similar?api_key=" + apiKey + "&language=en-US&page=1";
        return fetchJson(urlStr);
    }

    private JsonObject fetchJson(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        try (InputStreamReader reader = new InputStreamReader(conn.getInputStream())) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        }
    }
}
