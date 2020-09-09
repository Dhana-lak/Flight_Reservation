package com.airticket.reservation.services;

import com.airticket.reservation.dto.ReservationRequest;
import com.airticket.reservation.models.Flight;
import com.airticket.reservation.models.Passenger;
import com.airticket.reservation.models.Reservation;
import com.airticket.reservation.repositories.FlightRepository;
import com.airticket.reservation.repositories.PassengerRepository;
import com.airticket.reservation.repositories.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;


@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationServiceImpl.class);


    @Override
    public Reservation bookFlight(ReservationRequest reservationRequest) {

        LOGGER.info("Inside bookFlight()");
        Long flightId=reservationRequest.getFlightId();
        Optional<Flight> flightOptional=flightRepository.findById(flightId);
        LOGGER.info("Flight found with id: {}",flightId);
        Flight flight=flightOptional.get();
        Passenger passenger=new Passenger();
        passenger.setFirstName(reservationRequest.getPassengerFirstName());
        passenger.setMiddleName(reservationRequest.getPassengerMiddleName());
        passenger.setLastName(reservationRequest.getPassengerLastName());
        passenger.setEmail(reservationRequest.getPassengerEmail());
        passenger.setPhone(reservationRequest.getPassengerPhoneNumber());
        passenger.setNameOnTheCard(reservationRequest.getNameOnTheCard());
        passenger.setCardNumber(reservationRequest.getCardNumber());
        passenger.setExpirationDate(reservationRequest.getExpirationDate());
        passenger.setSecurityCode(reservationRequest.getSecurityCode());

        passengerRepository.save(passenger);
        LOGGER.info("Saved the passenger:" + passenger);

        Reservation reservation=new Reservation();
        reservation.setFlight(flight);
        reservation.setPassenger(passenger);
        reservation.setStatus("RESERVED");
        final Reservation savedReservation = reservationRepository.save(reservation);
        LOGGER.info("Saving the reservation:" + reservation);
        return savedReservation;

    }
}
