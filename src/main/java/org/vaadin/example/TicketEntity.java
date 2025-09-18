package org.vaadin.example;
import jakarta.persistence.*;

import java.time.LocalDateTime;
;
@Entity
@Table(name = "ticket")
public class TicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
     @Column(name="date")
    private LocalDateTime date ;
    // checked_in è un flag booleano nel DB
    @Column(name = "checked_in")
    private Boolean checkedIn;

    // checked_out è un datetime
    @Column(name = "checked_out")
    private LocalDateTime checkedOut;

    private String mess;

    protected TicketEntity() {} // costruttore richiesto da JPA

    public Long getId() { return id; }
    public Boolean getCheckedIn() { return checkedIn; }
    public LocalDateTime getCheckedOut() { return checkedOut; }
    public String getMess() { return mess; }
}


