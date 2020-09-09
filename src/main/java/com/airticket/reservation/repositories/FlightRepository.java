package com.airticket.reservation.repositories;

import com.airticket.reservation.models.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface FlightRepository extends JpaRepository<Flight,Long>{
    @Query("from Flight where departureCity=:departureCity and arrivalCity=:arrivalCity and dateOfDeparture=:dateOfDeparture" +
            " and status='ACTIVE'")
    List<Flight> findFlights(@Param("departureCity") String from, @Param("arrivalCity") String to,
                             @Param("dateOfDeparture") Date departureDate);

    @Query("from Flight where status='ACTIVE'")
    List<Flight> getAllFlights();
}
