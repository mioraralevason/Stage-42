package com.ecole._2.controller;

import com.ecole._2.models.Event;
import com.ecole._2.services.ApiService;
import com.ecole._2.services.EventService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class EventController {

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    @Autowired
    private EventService eventService;

    @Autowired
    private ApiService apiService;

    private static final String CAMPUS_ID = "65";  // fixed campus ID

    /**
     * Display the events page with optional date filters (GET request)
     */
    @GetMapping("/events")
    public String showEventsPage(
            @RequestParam(value = "beginAt", required = false) String beginAt,
            @RequestParam(value = "endAt", required = false) String endAt,
            HttpSession session,
            Model model
    ) {
        return fetchEvents(beginAt, endAt, session, model);
    }

    /**
     * Handle the form submission for filtering events (POST request)
     */
    @PostMapping("/events")
    public String filterEvents(
            @RequestParam(value = "beginAt", required = false) String beginAt,
            @RequestParam(value = "endAt", required = false) String endAt,
            HttpSession session,
            Model model
    ) {
        return fetchEvents(beginAt, endAt, session, model);
    }

    /**
     * Common method to fetch events and add them to the model
     */
    private String fetchEvents(String beginAt, String endAt, HttpSession session, Model model) {
        try {
            // Get access token
            String token = apiService.getAccessToken();

            // Fetch events from EventService
            List<Event> events = eventService.getAllEvents(
                    CAMPUS_ID,   // campusId fixed to 65
                    null,        // cursusId optional
                    null,        // userId optional
                    "begin_at",  // sort by begin_at
                    beginAt,
                    endAt,
                    token,
                    100          // page size
            );

            model.addAttribute("events", events);
            // Exemple pour formater la date en yyyy-MM-dd
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            if (beginAt != null) {
                model.addAttribute("beginAt", LocalDate.parse(beginAt).format(formatter));
            }
            if (endAt != null) {
                model.addAttribute("endAt", LocalDate.parse(endAt).format(formatter));
            }

            model.addAttribute("endAt", endAt);
            model.addAttribute("searchPerformed", true);

        } catch (Exception e) {
            logger.error("Error fetching events", e);
            model.addAttribute("error", "Error retrieving events: " + e.getMessage());
        }

        return "events-page"; // Thymeleaf template to display events
    }
}
