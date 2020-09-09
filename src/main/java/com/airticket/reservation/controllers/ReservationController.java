package com.airticket.reservation.controllers;

import com.airticket.reservation.dto.ReservationRequest;
import com.airticket.reservation.models.Flight;
import com.airticket.reservation.models.Passenger;
import com.airticket.reservation.models.Reservation;
import com.airticket.reservation.repositories.FlightRepository;
import com.airticket.reservation.repositories.PassengerRepository;
import com.airticket.reservation.repositories.ReservationRepository;
import com.airticket.reservation.services.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class ReservationController {
    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private ReservationService reservationService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationController.class);

    @RequestMapping("/showCompleteReservation")
    public String showCompleteReservation(@RequestParam("flightId") Long flightId, ModelMap modelMap){
        LOGGER.info("showCompleteReservation() invoked with the Flight Id: " + flightId);
        Optional<Flight> flight=flightRepository.findById(flightId);
        LOGGER.info("Flight found: {}",flight.toString());
        modelMap.addAttribute("flight",flight.get());
        modelMap.addAttribute("email", ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        return "reservation/completeReservation";
    }

    @RequestMapping(value = "/completeReservation",method = RequestMethod.POST)
    public String completeReservation(ReservationRequest reservationRequest, ModelMap modelMap){
        LOGGER.info("completeReservation() invoked with the Reservation: " + reservationRequest.toString());
        Reservation reservation=reservationService.bookFlight(reservationRequest);
        modelMap.addAttribute("msg","Reservation created successfully and the reservation id is "+reservation.getId());
        return getReservationListForUser(modelMap, reservation);
    }

    @RequestMapping("/showUpdateReservation")
    public String showUpdateReservation(@RequestParam("reservationId") Long reservationId, ModelMap modelMap){
        LOGGER.info("showUpdateReservation() invoked with the Reservation Id: " + reservationId);
        Optional<Reservation> reservation=reservationRepository.findById(reservationId);
        modelMap.addAttribute("flight",reservation.get().getFlight());
        modelMap.addAttribute("passenger", reservation.get().getPassenger());
        modelMap.addAttribute("reservationId", reservationId);
        return "reservation/editReservation";
    }

    @RequestMapping(value = "/updateReservation",method = RequestMethod.POST)
    public String updateReservation(ReservationRequest reservationRequest, ModelMap modelMap, @RequestParam("reservationId") Long reservationId){
        LOGGER.info("updateReservation() invoked with the Reservation: " + reservationRequest.toString());
        Reservation reservation = reservationRepository.findById(reservationId).get();
        Passenger passenger = reservation.getPassenger();
        passenger.setFirstName(reservationRequest.getPassengerFirstName());
        passenger.setMiddleName(reservationRequest.getPassengerMiddleName());
        passenger.setLastName(reservationRequest.getPassengerLastName());
        passenger.setPhone(reservationRequest.getPassengerPhoneNumber());
        passengerRepository.save(passenger);
        modelMap.addAttribute("msg","Reservation updated successfully and the reservation id is "+reservation.getId());
        return getReservationListForUser(modelMap, reservation);
    }

    @RequestMapping("/cancelReservation")
    public String cancelReservation(@RequestParam("reservationId") Long reservationId, ModelMap modelMap){
        LOGGER.info("showUpdateReservation() invoked with the Reservation Id: " + reservationId);
        Reservation reservation=reservationRepository.findById(reservationId).get();
        reservation.setStatus("CANCELLED");
        reservationRepository.save(reservation);
        modelMap.addAttribute("msg","Reservation cancelled successfully");
        return getReservationListForUser(modelMap, reservation);
    }

    private String getReservationListForUser(ModelMap modelMap, Reservation reservation) {
        String email = reservation.getPassenger().getEmail();
        List<Reservation> reservationList = reservationRepository.getReservations(email,"RESERVED");
        modelMap.addAttribute("reservations", reservationList);
        reservationList = reservationRepository.getReservations(email, "CANCELLED");
        modelMap.addAttribute("cancellations", reservationList);
        return "flights/findFlights";
    }

}
