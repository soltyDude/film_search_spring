package com.example.filmservice.tmdb;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Component
public class TMDBClient {

    @Value("${tmdb.api_key}")
    private String apiKey;

    @Value("${tmdb.base_url}")
    private String baseUrl;

    public JsonObject fetchMovieDetails(int apiId) throws Exception {
        String urlStr = baseUrl + "/movie/" + apiId + "?api_key=" + apiKey + "&language=en-US";
        return fetchJson(urlStr);
    }

    public JsonObject searchMovies(String query) throws Exception {
        String urlStr = baseUrl + "/search/movie?api_key=" + apiKey + "&language=en-US&query=" +
                java.net.URLEncoder.encode(query, StandardCharsets.UTF_8);
        return fetchJson(urlStr);
    }

    public JsonObject fetchPopularMovies() throws Exception {
        String urlStr = baseUrl + "/movie/popular?api_key=" + apiKey + "&language=en-US";
        return fetchJson(urlStr);
    }

    private JsonObject fetchJson(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept-Charset", "UTF-8");
        conn.connect();

        try (InputStreamReader reader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        }
    }

    public JsonObject fetchSimilarMovies(int apiId) throws Exception {
        String urlStr = baseUrl + "/movie/" + apiId + "/similar?api_key=" + apiKey + "&language=en-US&page=1";
        return fetchJson(urlStr);
    }

}
