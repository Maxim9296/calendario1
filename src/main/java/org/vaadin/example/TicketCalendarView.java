package org.vaadin.example;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

@Route("")
public class TicketCalendarView extends VerticalLayout {

    private YearMonth currentMonth;
    private final Div calendarContainer;
    private final H2 monthTitle;

    public TicketCalendarView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        currentMonth = YearMonth.now(); // mese corrente
        calendarContainer = new Div();
        monthTitle = new H2();

        // pulsanti navigazione
        Button prev = new Button("<", e -> showMonth(currentMonth.minusMonths(1)));
        Button next = new Button(">", e -> showMonth(currentMonth.plusMonths(1)));

        HorizontalLayout header = new HorizontalLayout(prev, monthTitle, next);
        header.setAlignItems(Alignment.CENTER);
        header.setSpacing(true);

        add(header, calendarContainer);

        showMonth(currentMonth);
    }

    private void showMonth(YearMonth month) {
        this.currentMonth = month;
        this.monthTitle.setText(month.getMonth().toString() + " " + month.getYear());

        calendarContainer.removeAll();
        calendarContainer.add(buildCalendar(month, getTicketData()));
    }

    private Div buildCalendar(YearMonth month, Map<LocalDate, Ticket> tickets) {
        Div calendar = new Div();
        calendar.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(7, 1fr)")
                .set("gap", "5px")
                .set("background-color", "#f0f0f0")
                .set("padding", "10px");

        // intestazioni giorni della settimana
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String d : days) {
            Span header = new Span(d);
            header.getStyle()
                    .set("font-weight", "bold")
                    .set("text-align", "center");
            calendar.add(header);
        }

        // primo giorno del mese
        LocalDate firstDay = month.atDay(1);
        int firstDayOfWeek = firstDay.getDayOfWeek().getValue() % 7; // domenica=0

        // spazi vuoti prima del 1° giorno
        for (int i = 0; i < firstDayOfWeek; i++) {
            calendar.add(new Div());
        }

        // giorni del mese
        for (int day = 1; day <= month.lengthOfMonth(); day++) {
            LocalDate current = month.atDay(day);
            Div cell = new Div();
            cell.getStyle()
                    .set("border", "1px solid #ccc")
                    .set("border-radius", "8px")
                    .set("min-height", "80px")
                    .set("padding", "4px");

            Span label = new Span(String.valueOf(day));
            label.getStyle().set("font-weight", "bold");
            cell.add(label);

            if (tickets.containsKey(current)) {
                Ticket t = tickets.get(current);
                Span ticketInfo = new Span(t.getCheckedIn() + "/" + t.getTotal() + " Ticket");

                String bgColor = t.isComplete() ? "#4CAF50" :
                        (t.getCheckedIn() == 0 ? "#F44336" : "#FF9800");

                ticketInfo.getStyle()
                        .set("display", "block")
                        .set("margin-top", "4px")
                        .set("padding", "2px 4px")
                        .set("background-color", bgColor)
                        .set("color", "white")
                        .set("border-radius", "4px")
                        .set("font-size", "12px");

                cell.add(ticketInfo);
            }

            calendar.add(cell);
        }

        return calendar;
    }

    private Map<LocalDate, Ticket> getTicketData() {
        Map<LocalDate, Ticket> map = new HashMap<>();
        // dati demo
        map.put(LocalDate.of(2025, 9, 3), new Ticket(23, 25));
        map.put(LocalDate.of(2025, 9, 4), new Ticket(22, 25));
        map.put(LocalDate.of(2025, 9, 5), new Ticket(0, 22));
        map.put(LocalDate.of(2025, 9, 15), new Ticket(12, 15));
        map.put(LocalDate.of(2025, 9, 20), new Ticket(20, 20));
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
