package eventcalendar.eventcalendar.eventcalendar;

import org.joda.time.LocalDate;

public final class Event {

    private final LocalDate date;
    private final int color;

    public Event(LocalDate date, int color) {
        this.date = date;
        this.color = color;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        return date != null ? date.equals(event.date) : event.date == null;

    }

    @Override
    public int hashCode() {
        return date != null ? date.hashCode() : 0;
    }

}
