package com.ecole._2.controller;

import com.ecole._2.models.Event;
import com.ecole._2.services.ApiService;
import com.ecole._2.services.EventService;
import com.ecole._2.utils.EventCsvExporter;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
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

    @GetMapping("/events/export")
    public void exportEvents(HttpServletResponse response, HttpSession session, Model model) throws IOException {
        // Forcer UTF-8
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=events.csv");

        List<Event> events = (List<Event>) session.getAttribute("events");

        if (events == null || events.isEmpty()) {
            model.addAttribute("error", "CSV cannot be generated");
            fetchEvents(null, null, session, model);
            return;
        }

        try (OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8)) {
            // BOM pour Excel
            writer.write('\ufeff');
            EventCsvExporter.writeEventsToCsv(events, writer);
        }
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

            session.setAttribute("events", events);
            model.addAttribute("events", events);
            model.addAttribute("beginAt", beginAt);
            model.addAttribute("endAt", endAt);
            model.addAttribute("searchPerformed", true);

        } catch (Exception e) {
            logger.error("Error fetching events", e);
            model.addAttribute("error", "Error retrieving events: " + e.getMessage());
        }

        return "events-page"; // Thymeleaf template to display events
    }
}
