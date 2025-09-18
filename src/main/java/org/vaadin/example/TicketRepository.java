package org.vaadin.example;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TicketRepository extends JpaRepository<TicketEntity, Long> {
    @Query("""
            SELECT t FROM TicketEntity t
            WHERE (t.date BETWEEN :start AND :end)
               OR (t.checkedOut BETWEEN :start AND :end)
            """)
    List<TicketEntity> findOpenedOrClosedBetween(@Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end);
}
