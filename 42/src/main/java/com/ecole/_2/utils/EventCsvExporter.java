package com.ecole._2.utils;

import com.ecole._2.models.Event;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EventCsvExporter {

    public static void writeEventsToCsv(List<Event> events, Writer writer) throws IOException {
        // Forcer UTF-8
        writer.write('\ufeff'); // BOM UTF-8 pour Excel

        // En-tête
        writer.write("id,name,description,location,kind,max_people,nbr_subscribers,begin_at,end_at\n");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Event event : events) {
            writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                    escapeCsv(event.getId()),
                    escapeCsv(event.getName()),
                    escapeCsv(event.getDescription()),
                    escapeCsv(event.getLocation()),
                    escapeCsv(event.getKind()),
                    event.getMaxPeople() != null ? event.getMaxPeople().toString() : "",
                    event.getNbrSubscribers() != null ? event.getNbrSubscribers().toString() : "",
                    event.getBeginAt() != null ? formatter.format(event.getBeginAt()) : "",
                    event.getEndAt() != null ? formatter.format(event.getEndAt()) : ""
            ));
        }
    }

    private static String escapeCsv(String value) {
        if (value == null) return "";
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\n") || escaped.contains(";")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}
