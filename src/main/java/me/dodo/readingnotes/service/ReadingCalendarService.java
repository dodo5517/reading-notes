package me.dodo.readingnotes.service;

import me.dodo.readingnotes.dto.calendar.CalendarResponse;
import me.dodo.readingnotes.dto.calendar.CalendarSummary;
import me.dodo.readingnotes.dto.calendar.DayStat;
import me.dodo.readingnotes.repository.DayCountRow;
import me.dodo.readingnotes.repository.ReadingRecordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReadingCalendarService {

    private final ReadingRecordRepository repo;

    public ReadingCalendarService(ReadingRecordRepository repo) {
        this.repo = repo;
    }

    public CalendarResponse getMonthly(Long userId, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);

        // 그 달의 첫째 날
        LocalDate startDate = ym.atDay(1);
        // 그 달의 마지막 날
        LocalDate endDate = ym.plusMonths(1).atDay(1);

        // time도 붙임
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end   = endDate.atStartOfDay();

        List<DayCountRow> rows = repo.countByDayInRange(userId, start, end);

        List<DayStat> days = new ArrayList<>();
        long totalRecords = 0;
        for (DayCountRow r : rows) {
            days.add(new DayStat(r.getDay(), r.getCnt()));
            totalRecords += r.getCnt();
        }

        int totalDaysWithRecord = days.size();
        String first = totalDaysWithRecord == 0 ? null : days.get(0).getDate().toString();
        String last  = totalDaysWithRecord == 0 ? null : days.get(totalDaysWithRecord - 1).getDate().toString();

        CalendarSummary summary = new CalendarSummary(totalDaysWithRecord, totalRecords, first, last);
        return new CalendarResponse(startDate, endDate, days, summary);
    }
}
