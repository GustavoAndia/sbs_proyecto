package com.bizlinks.sbs_proyecto.data;
import java.time.DayOfWeek;
import java.time.LocalDate;
/**
 *
 * @author GustavoAndia
 */
public class Weekday {
    private LocalDate currentDay;
    private LocalDate before;

    public Weekday() {
        this.currentDay = LocalDate.now();
        this.before = currentDay.minusDays(0);
    }

    public int getCurrentDayOfWeek() {
        return currentDay.getDayOfWeek().getValue();
    }

    public String getCurrentDayName() {
        return currentDay.getDayOfWeek().toString();
    }

    public boolean isWeekend() {
        return currentDay.getDayOfWeek() == DayOfWeek.SATURDAY || currentDay.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    public String[] getFechas() {
        String fecha2 = currentDay.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String fecha1 = before.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return new String[] { fecha1, fecha2 };
    }
}
