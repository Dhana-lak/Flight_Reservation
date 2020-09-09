package com.airticket.reservation.services;

import com.airticket.reservation.dto.ReservationRequest;
import com.airticket.reservation.models.Reservation;


public interface ReservationService {
    Reservation bookFlight(ReservationRequest reservationRequest);
}
