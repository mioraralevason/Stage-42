package com.ecole._2.services;

import com.ecole._2.models.Event;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventService {

    private static final String BASE_URL = "https://api.intra.42.fr";

    private final HttpClient client;
    private final ObjectMapper mapper;

    public EventService() {
        this.client = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();

        // ⚡ Support Java 8 Date/Time (OffsetDateTime, LocalDateTime, etc.)
        this.mapper.registerModule(new JavaTimeModule());
    }

    /**
     * Fetch all events with optional filters and pagination.
     *
     * @param campusId Campus ID filter (optional)
     * @param cursusId Cursus ID filter (optional)
     * @param userId   User ID filter (optional)
     * @param sort     Field to sort by (optional)
     * @param beginAt  Start datetime (ISO 8601) for range filter (optional)
     * @param endAt    End datetime (ISO 8601) for range filter (optional)
     * @param token    Bearer token for authentication
     * @param pageSize Number of events per page (max 100)
     * @return List of Event objects
     * @throws IOException
     * @throws InterruptedException
     */
    public List<Event> getAllEvents(
            String campusId,
            String cursusId,
            String userId,
            String sort,
            String beginAt,
            String endAt,
            String token,
            int pageSize
    ) throws IOException, InterruptedException {

        List<Event> allEvents = new ArrayList<>();
        int page = 1;

        while (true) {
            StringBuilder urlBuilder = new StringBuilder(BASE_URL + "/v2/events?page[size]=" + pageSize + "&page[number]=" + page);

            if (campusId != null) urlBuilder.append("&campus_id=").append(campusId);
            if (cursusId != null) urlBuilder.append("&cursus_id=").append(cursusId);
            if (userId != null) urlBuilder.append("&user_id=").append(userId);
            if (sort != null) urlBuilder.append("&sort=").append(sort);
            if (beginAt != null && endAt != null) {
                urlBuilder.append("&range[begin_at]=").append(beginAt).append(",").append(endAt);
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlBuilder.toString()))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            // Small delay to respect API rate limits
            Thread.sleep(125);

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error fetching events: " + response.body());
            }

            // Deserialize JSON array directly into List<Event>
            List<Event> events = mapper.readValue(response.body(), new TypeReference<List<Event>>() {});
            if (events.isEmpty()) break;

            allEvents.addAll(events);
            System.out.println("Fetched page " + page + ", got " + events.size() + " events");
            page++;
        }

        System.out.println("Total events fetched = " + allEvents.size());
        return allEvents;
    }
}
