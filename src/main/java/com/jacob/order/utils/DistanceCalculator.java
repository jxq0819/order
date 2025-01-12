package com.jacob.order.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;

@Component
public final class DistanceCalculator {
    private static final String API_STATUS_OK = "OK";
    private static final String URL_TEMPLATE = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=%f,%f&destinations=%f,%f&key=%s";

    /**
     * Retrieves the distance between origin and destination coordinates using Google Maps Distance Matrix API.
     *
     * @param originLat      Latitude of the origin.
     * @param originLng      Longitude of the origin.
     * @param destinationLat Latitude of the destination.
     * @param destinationLng Longitude of the destination.
     * @param apiKey         Google Maps API key.
     * @return The distance in meters if successful; -1 otherwise.
     */
    public int getDistance(BigDecimal originLat, BigDecimal originLng,
                           BigDecimal destinationLat, BigDecimal destinationLng,
                           String apiKey) {
        try {
            // Format the URL with the provided coordinates and API key
            String url = String.format(URL_TEMPLATE, originLat, originLng, destinationLat, destinationLng, apiKey);
            // Fetch the API response
            ResponseEntity<String> response = fetchApiResponse(url);
            // Parse and return the distance value
            return parseDistance(response);
        } catch (Exception e) {
            // Log the exception (optional) and return -1 to indicate failure
            // You can use a logging framework like SLF4J for better logging
            return -1;
        }
    }

    /**
     * Makes an HTTP GET request to the specified URL.
     *
     * @param url The URL to send the GET request to.
     * @return The response entity containing the JSON response as a string.
     */
    private ResponseEntity<String> fetchApiResponse(String url) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForEntity(URI.create(url), String.class);
    }

    /**
     * Parses the JSON response to extract the distance value.
     *
     * @param response The response entity containing the JSON response.
     * @return The distance in meters if successful; -1 otherwise.
     */
    private int parseDistance(ResponseEntity<String> response) {
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            return -1;
        }

        // Parse the JSON response using Fastjson2
        JSONObject jsonResponse = JSON.parseObject(response.getBody());

        // Check if the API status is OK
        if (!API_STATUS_OK.equals(jsonResponse.getString("status"))) {
            return -1;
        }

        // Navigate through the JSON structure to extract the distance value
        // JSON Path: rows[0].elements[0].distance.value
        try {
            JSONObject row = jsonResponse.getJSONArray("rows").getJSONObject(0);
            JSONObject element = row.getJSONArray("elements").getJSONObject(0);

            // Check if the element status is OK
            if (!API_STATUS_OK.equals(element.getString("status"))) {
                return -1;
            }

            JSONObject distance = element.getJSONObject("distance");
            return distance.getIntValue("value"); // Distance in meters
        } catch (Exception e) {
            // Handle any parsing exceptions
            return -1;
        }
    }
}