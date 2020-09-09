package com.airticket.reservation.controllers;

import com.airticket.reservation.models.Flight;
import com.airticket.reservation.models.Reservation;
import com.airticket.reservation.models.User;
import com.airticket.reservation.repositories.FlightRepository;
import com.airticket.reservation.repositories.ReservationRepository;
import com.airticket.reservation.repositories.UserRepository;
import com.airticket.reservation.services.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    private SecurityService securityService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @RequestMapping("/showReg")
    public String showRegistrationPage() {
        LOGGER.info("Inside showRegistrationPage()");
        return "login/registerUser";
    }

    @RequestMapping("/showIndex")
    public String showIndex() {
        return "index";
    }

    @RequestMapping(value = "/registerUser", method = RequestMethod.POST)
    public String register(@ModelAttribute("user") User user, ModelMap modelmap) {
        LOGGER.info("{} Inside register()", user.getEmail());
        Optional<User> foundUser= userRepository.findByEmail(user.getEmail());
        if(foundUser.isPresent()){
            LOGGER.error("User is already registered with email {} ",user.getEmail());
            modelmap.addAttribute("msg", "User with the given email id already exists.");
            return "login/registerUser";
        }
        LOGGER.info("Email Exists: "+user.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRole("PASSENGER");
        userRepository.save(user);
        modelmap.addAttribute("msg", "User registered successfully.");
        return "login/login";
    }

    @RequestMapping("/showLogin")
    public String showLoginPage() {
        LOGGER.info("Inside showLoginPage()");
        return "login/login";
    }

    @RequestMapping(value="/login",method = RequestMethod.POST)
    public String login(@RequestParam("email") String email, @RequestParam("password") String password, ModelMap modelMap){
        LOGGER.info("{} Inside login()",email);
        Optional<User> foundUser=userRepository.findByEmail(email);
        if(!foundUser.isPresent()){
            modelMap.addAttribute("msg","Invalid username.");
        } else {
            LOGGER.info("Email Exists: "+email);
            try {
                boolean loginResponse = securityService.login(email, password);
                if (loginResponse) {
                    if("PASSENGER".equalsIgnoreCase(foundUser.get().getRole())) {
                        modelMap.addAttribute("msg", "Logged in Successfully");
                        return getReservationList(email, modelMap);
                    } else {
                        List<Flight> flightList = flightRepository.getAllFlights();
                        modelMap.addAttribute("flights", flightList);
                        return "flights/addFlight";
                    }
                } else {
                    LOGGER.info("User entered Invalid credentials email:{} and password:{}", email, password);
                    modelMap.addAttribute("msg", "Invalid username or password");
                }
            } catch(BadCredentialsException e) {
                modelMap.addAttribute("msg", "Invalid password");
            }
        }
        return "login/login";
    }

    @RequestMapping(value="/redirectToLandingPage")
    public String redirectToLandingPage(ModelMap modelMap) {
        String email = ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return getReservationList(email, modelMap);
    }

    private String getReservationList(@RequestParam("email") String email, ModelMap modelMap) {
        List<Reservation> reservationList = reservationRepository.getReservations(email,"RESERVED");
        modelMap.addAttribute("reservations", reservationList);
        reservationList = reservationRepository.getReservations(email, "CANCELLED");
        modelMap.addAttribute("cancellations", reservationList);
        return "flights/findFlights";
    }
}
