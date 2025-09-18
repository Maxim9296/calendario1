package org.vaadin.example;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route("")
public class TicketCalendarView extends VerticalLayout {
    private LocalDate currentStart;
    private final Div calendarContainer;
    private final H2 calendarTitle;
    private final TicketRepository ticketRepository;
    private boolean isMonthlyView = false;

    public TicketCalendarView(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        getStyle().set("background", "linear-gradient(135deg, #6d90b9, #bbc7dc)");
        LocalDate  today = LocalDate.now();
        currentStart =today;
        calendarContainer = new Div();
        calendarTitle = new H2();

        Button prev = new Button("<", e -> navigate(-1));
        Button next = new Button(">", e -> navigate(1));
        Button toggleView = new Button("Toggle View", e -> {
            isMonthlyView = !isMonthlyView;
            showCalendar(today);
        });

        HorizontalLayout header = new HorizontalLayout(prev, calendarTitle, next, toggleView);
        header.setAlignItems(Alignment.CENTER);
        header.setSpacing(true);

        add(header, calendarContainer);

        showCalendar(today);
    }

    private void navigate(int step) {
        currentStart = isMonthlyView
                ? currentStart.plusMonths(step)
                : currentStart.plusWeeks(step);
        showCalendar(currentStart);
    }

    private void showCalendar(LocalDate startDate) {
        if (isMonthlyView) {
            currentStart = startDate.withDayOfMonth(1);
            calendarTitle.setText("Month: " + currentStart.getMonth() + " " + currentStart.getYear());
            calendarContainer.removeAll();
            calendarContainer.add(buildMonthCalendar(currentStart, getMonthlyTicketData()));
        } else {
            // Calcola inizio della settimana qui
            currentStart = startDate.minusDays(startDate.getDayOfWeek().getValue() % 7);
            LocalDate endDate = currentStart.plusDays(6);
            calendarTitle.setText("Week: " + currentStart + " → " + endDate);
            calendarContainer.removeAll();
            calendarContainer.add(buildWeekCalendar(currentStart, getWeeklyTicketData()));
        }
    }

    private Map<LocalDate, Ticket> getWeeklyTicketData() {
        return getTicketData(currentStart, currentStart.plusDays(6));
    }

    private Map<LocalDate, Ticket> getMonthlyTicketData() {
        LocalDate start = currentStart.withDayOfMonth(1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return getTicketData(start, end);
    }

    private Map<LocalDate, Ticket> getTicketData(LocalDate start, LocalDate end) {
        Map<LocalDate, Ticket> map = new HashMap<>();
        List<TicketEntity> entries = ticketRepository.findOpenedOrClosedBetween(
                start.atStartOfDay(), end.atTime(23, 59, 59));

        for (TicketEntity entry : entries) {
            LocalDate openDate = entry.getDate().toLocalDate();

            if (!openDate.isBefore(start) && !openDate.isAfter(end)) {
                Ticket t = map.computeIfAbsent(openDate, d -> new Ticket(0, 0));
                t.incrementTotal();
                if (Boolean.TRUE.equals(entry.getCheckedIn())) {
                    t.incrementIn();
                }
                if (entry.getCheckedOut() != null) {
                    t.markComplete();
                }
            }
        }

        return map;
    }
    private Div buildWeekCalendar(LocalDate start, Map<LocalDate, Ticket> tickets) {
        Div calendar = new Div();
        calendar.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(7, 120px)")
                .set("gap", "8px")
                .set("background-color", "#ffffff")
                .set("padding", "12px")
                .set("justify-content", "center");

        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String d : days) {
            Span header = new Span(d);
            header.getStyle()
                    .set("font-weight", "600")
                    .set("text-align", "center")
                    .set("padding", "6px 0")
                    .set("background-color", "#e0e0e0")
                    .set("border-radius", "6px")
                    .set("font-size", "14px");
            calendar.add(header);
        }

        for (int i = 0; i < 7; i++) {
            LocalDate current = start.plusDays(i);
            calendar.add(createCalendarCell(current, tickets.get(current)));
        }

        return calendar;
    }

    private Div buildMonthCalendar(LocalDate monthStart, Map<LocalDate, Ticket> tickets) {
        Div calendar = new Div();
        calendar.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(7, 120px)")
                .set("gap", "8px")
                .set("background-color", "#ffffff")
                .set("padding", "12px")
                .set("justify-content", "center");

        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String d : days) {
            Span header = new Span(d);
            header.getStyle()
                    .set("font-weight", "600")
                    .set("text-align", "center")
                    .set("padding", "6px 0")
                    .set("background-color", "#e0e0e0")
                    .set("border-radius", "6px")
                    .set("font-size", "14px");
            calendar.add(header);
        }

        int dayOfWeekOffset = monthStart.getDayOfWeek().getValue() % 7; // Sunday = 0
        for (int i = 0; i < dayOfWeekOffset; i++) {
            Div emptyCell = new Div();
            emptyCell.setHeight("100px");
            emptyCell.setWidth("120px");
            calendar.add(emptyCell);
        }

        int daysInMonth = monthStart.lengthOfMonth();
        for (int i = 1; i <= daysInMonth; i++) {
            LocalDate current = monthStart.withDayOfMonth(i);
            calendar.add(createCalendarCell(current, tickets.get(current)));
        }

        return calendar;
    }

    private Div createCalendarCell(LocalDate date, Ticket ticket) {
        Div cell = new Div();
        cell.getStyle()
                .set("border", "1px solid #ddd")
                .set("border-radius", "8px")
                .set("min-height", "100px")
                .set("width", "100px")
                .set("padding", "8px")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "center")
                .set("justify-content", "flex-start")
                .set("background-color", "#fafafa")
                .set("transition", "transform 0.2s")
                .set("cursor", "pointer");

        // Effetto hover
        cell.getElement().getStyle().set("hover", "transform: scale(1.03)");

        Span label = new Span(date.getDayOfMonth() + "/" + date.getMonthValue());
        label.getStyle()
                .set("font-weight", "600")
                .set("margin-bottom", "6px")
                .set("font-size", "14px");
        cell.add(label);

        if (ticket != null) {
            Span ticketInfo = new Span(ticket.getCheckedIn() + "/" + ticket.getTotal() + " Tickets");

            String bgColor = "#F44336"; // rosso
            if (ticket.isComplete()) {
                bgColor = "#4CAF50"; // verde
            } else if (ticket.getCheckedIn() > 0) {
                bgColor = "#FF9800"; // arancione
            }

            ticketInfo.getStyle()
                    .set("display", "block")
                    .set("margin-top", "auto")
                    .set("padding", "4px 8px")
                    .set("background-color", bgColor)
                    .set("color", "white")
                    .set("border-radius", "4px")
                    .set("font-size", "12px")
                    .set("text-align", "center");

            cell.add(ticketInfo);
        }

        return cell;
    }

    // Classe Ticket interna invariata
    public static class Ticket {
        private int checkedIn;
        private int total;
        private boolean complete;

        public Ticket(int checkedIn, int total) {
            this.checkedIn = checkedIn;
            this.total = total;
            this.complete = false;
        }

        public void incrementIn() {
            checkedIn++;
        }

        public void incrementTotal() {
            total++;
        }

        public void markComplete() {
            this.complete = true;
        }

        public int getCheckedIn() {
            return checkedIn;
        }

        public int getTotal() {
            return total;
        }

        public boolean isComplete() {
            return complete;
        }
    }
}
