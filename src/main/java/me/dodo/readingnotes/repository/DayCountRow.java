package me.dodo.readingnotes.repository;

import java.time.LocalDate;

public interface DayCountRow {
    LocalDate getDay();
    long getCnt();
}
