package me.dodo.readingnotes.dto.calendar;

public class CalendarSummary {
    private int totalDaysWithRecord;
    private long totalRecords;
    private String firstRecordedAt; // YYYY-MM-DD
    private String lastRecordedAt;  // YYYY-MM-DD

    public CalendarSummary(int totalDaysWithRecord, long totalRecords, String firstRecordedAt, String lastRecordedAt) {
        this.totalDaysWithRecord = totalDaysWithRecord;
        this.totalRecords = totalRecords;
        this.firstRecordedAt = firstRecordedAt;
        this.lastRecordedAt = lastRecordedAt;
    }
    public int getTotalDaysWithRecord() { return totalDaysWithRecord; }
    public long getTotalRecords() { return totalRecords; }
    public String getFirstRecordedAt() { return firstRecordedAt; }
    public String getLastRecordedAt() { return lastRecordedAt; }
}
