package com.airticket.reservation.repositories;

import com.airticket.reservation.models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation,Long> {
    @Query("from Reservation resv where resv.passenger.email=:email and resv.status=:status")
    List<Reservation> getReservations(@Param("email") String email, @Param("status") String status);
}
