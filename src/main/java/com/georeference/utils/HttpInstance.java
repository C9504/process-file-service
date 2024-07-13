package com.georeference.utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpInstance {
    private static HttpClient instance;

    private HttpInstance() {

    }

    public static HttpClient getInstance() {
        if (instance == null) {
            return HttpClient.newHttpClient();
        }
        return instance;
    }

    public static String sendGet(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();
        HttpResponse<String> response = getInstance().send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
