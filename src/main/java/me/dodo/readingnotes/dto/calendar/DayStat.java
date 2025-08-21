package me.dodo.readingnotes.dto.calendar;

import java.time.LocalDate;

public class DayStat {
    private LocalDate date;
    private long count;

    public DayStat(LocalDate date, long count) {
        this.date = date;
        this.count = count;
    }
    public LocalDate getDate() { return date; }
    public long getCount() { return count; }
}
