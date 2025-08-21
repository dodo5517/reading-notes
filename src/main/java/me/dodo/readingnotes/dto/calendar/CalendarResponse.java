package me.dodo.readingnotes.dto.calendar;

import java.time.LocalDate;
import java.util.List;

public class CalendarResponse {
    private LocalDate rangeStart;
    private LocalDate rangeEndExclusive;
    private List<DayStat> days;
    private CalendarSummary summary;

    public CalendarResponse(LocalDate rangeStart, LocalDate rangeEndExclusive,
                            List<DayStat> days, CalendarSummary summary) {
        this.rangeStart = rangeStart;
        this.rangeEndExclusive = rangeEndExclusive;
        this.days = days;
        this.summary = summary;
    }
    public LocalDate getRangeStart() { return rangeStart; }
    public LocalDate getRangeEndExclusive() { return rangeEndExclusive; }
    public List<DayStat> getDays() { return days; }
    public CalendarSummary getSummary() { return summary; }
}
