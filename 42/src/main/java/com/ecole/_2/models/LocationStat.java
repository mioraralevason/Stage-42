package com.ecole._2.models;

import java.time.Duration;
import java.time.LocalDate;

public class LocationStat {
    private LocalDate date;
    private Duration duration;

    public LocationStat() {}

    public LocationStat(LocalDate date, Duration duration) {
        this.date = date;
        this.duration = duration;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
}
