package com.example.filmservice.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.web.client.RestTemplate;

public class TMDBApiUtil {

    private static final String API_KEY = "e28fe83118014486bd75c60ecd32ede4"; // Укажи API-ключ
    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/";

    public static JsonObject fetchMovieDetails(int apiId) {
        RestTemplate restTemplate = new RestTemplate();
        String url = BASE_URL + apiId + "?api_key=" + API_KEY;
        String response = restTemplate.getForObject(url, String.class);
        return JsonParser.parseString(response).getAsJsonObject();
    }

    public static JsonObject fetchMoviesByQuery(String query) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.themoviedb.org/3/search/movie?query=" + query + "&api_key=" + API_KEY;
        String response = restTemplate.getForObject(url, String.class);
        return JsonParser.parseString(response).getAsJsonObject();
    }

}
