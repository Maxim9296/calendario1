package org.vaadin.example;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Route("")
public class TicketCalendarView extends VerticalLayout {

    private LocalDate weekStart;
    private final Div calendarContainer;
    private final H2 weekTitle;

    public TicketCalendarView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);


        LocalDate today = LocalDate.now();
        weekStart = today.minusDays(today.getDayOfWeek().getValue() % 7);

        calendarContainer = new Div();
        weekTitle = new H2();


        Button prev = new Button("<", e -> showWeek(weekStart.minusWeeks(1)));
        Button next = new Button(">", e -> showWeek(weekStart.plusWeeks(1)));

        HorizontalLayout header = new HorizontalLayout(prev, weekTitle, next);
        header.setAlignItems(Alignment.CENTER);
        header.setSpacing(true);

        add(header, calendarContainer);

        showWeek(weekStart);
    }

    private void showWeek(LocalDate startDate) {
        this.weekStart = startDate;

        LocalDate endDate = weekStart.plusDays(6);
        weekTitle.setText("Week: " + weekStart + " → " + endDate);

        calendarContainer.removeAll();
        calendarContainer.add(buildWeekCalendar(weekStart, getTicketData()));
    }

    private Div buildWeekCalendar(LocalDate start, Map<LocalDate, Ticket> tickets) {
        Div calendar = new Div();
        calendar.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(7, 1fr)")
                .set("gap", "5px")
                .set("background-color", "#f0f0f0")
                .set("padding", "10px")
                .set("min-width", "100px");



        // intestazioni giorni della settimana
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String d : days) {
            Span header = new Span(d);
            header.getStyle()
                    .set("font-weight", "bold")
                    .set("text-align", "center");
            calendar.add(header);
        }

        // giorni della settimana
        for (int i = 0; i < 7; i++) {
            LocalDate current = start.plusDays(i);

            Div cell = new Div();
            cell.getStyle()
                    .set("border", "1px solid #ccc")
                    .set("border-radius", "8px")
                    .set("min-height", "100px")
                    .set("padding", "6px")
                   .set("weight","100px");
            Span label = new Span(current.getDayOfMonth() + "/" + current.getMonthValue());
            label.getStyle().set("font-weight", "bold");
            cell.add(label);

            if (tickets.containsKey(current)) {
                Ticket t = tickets.get(current);
                Span ticketInfo = new Span(t.getCheckedIn() + "/" + t.getTotal() + " Ticket");

                String bgColor = t.isComplete() ? "#4CAF50" :
                        (t.getCheckedIn() == 0 ? "#F44336" : "#FF9800");

                ticketInfo.getStyle()
                        .set("display", "block")
                        .set("margin-top", "6px")
                        .set("padding", "4px 6px")
                        .set("background-color", bgColor)
                        .set("color", "white")
                        .set("border-radius", "4px")
                        .set("font-size", "13px");

                cell.add(ticketInfo);
            }

            calendar.add(cell);
        }

        return calendar;
    }

    private Map<LocalDate, Ticket> getTicketData() {
        Map<LocalDate, Ticket> map = new HashMap<>();
        // dati demo
        map.put(LocalDate.of(2025, 9, 15), new Ticket(12, 15));
        map.put(LocalDate.of(2025, 9, 16), new Ticket(8, 20));
        map.put(LocalDate.of(2025, 9, 18), new Ticket(0, 10));
        map.put(LocalDate.of(2025, 9, 19), new Ticket(20, 20));
        return map;
    }

    // Classe Ticket
    public static class Ticket {
        private final int checkedIn;
        private final int total;

        public Ticket(int checkedIn, int total) {
            this.checkedIn = checkedIn;
            this.total = total;
        }

        public int getCheckedIn() { return checkedIn; }
        public int getTotal() { return total; }
        public boolean isComplete() { return checkedIn == total; }
    }
}
