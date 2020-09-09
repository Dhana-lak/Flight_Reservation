package com.airticket.reservation.controllers;

import com.airticket.reservation.models.Flight;
import com.airticket.reservation.repositories.FlightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

@Controller
public class FlightController {

    private static final Logger LOGGER= LoggerFactory.getLogger(FlightController.class);

    @Autowired
    FlightRepository flightRepository;

    @RequestMapping("/findFlights")
    public String findFLights(@RequestParam("source") String source, @RequestParam("destination") String destination,
                              @RequestParam("departDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date departDate, ModelMap modelMap) {

        LOGGER.info("Inside findFlights() From:" + source + " TO:" + destination + "Departure Date: " + departDate);
        List<Flight> flights =flightRepository.findFlights(source,destination,departDate);
        modelMap.addAttribute("flights",flights);
        LOGGER.info("Flights found are"+flights.toString());
        return "flights/displayFlights";
    }

    @RequestMapping("/admin/showAddFlight")
    public String showAddFlightPage(){
        return "flights/addFlight";
    }

    @RequestMapping("/admin/addFlight")
    public String addFlight(@ModelAttribute("flight") Flight flight,ModelMap modelmap){
        flight.setStatus("ACTIVE");
        flightRepository.save(flight);
        modelmap.addAttribute("msg","Flight Added Successfully");
        return getAllFlights(modelmap);
    }
    @RequestMapping("/deleteFlight")
    public String deleteFlight(@RequestParam("flightId") Long flightId, ModelMap modelMap){
        Flight flight= flightRepository.findById(flightId).get();
        flight.setStatus("INACTIVE");
        flightRepository.save(flight);
        modelMap.addAttribute("msg","Flight deleted successfully");
        return getAllFlights(modelMap);
    }
    private String getAllFlights(ModelMap modelMap) {
        List<Flight> flightList = flightRepository.getAllFlights();
        modelMap.addAttribute("flights", flightList);
        return "flights/addFlight";

}}
