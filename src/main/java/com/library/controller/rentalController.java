package com.library.controller;

import com.library.model.Book;
import com.library.model.Client;
import com.library.model.Rental;
import com.library.repository.BookRepo;
import com.library.repository.ClientRepo;
import com.library.repository.RentalRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.DAYS;

@Controller
public class rentalController {
    class newRental{
        Long bookId;
        Long weeks;
        Boolean reserve;

        public Long getBookId() {
            return bookId;
        }

        public void setBookId(Long bookId) {
            this.bookId = bookId;
        }

        public Long getWeeks() {
            return weeks;
        }

        public void setWeeks(Long weeks) {
            this.weeks = weeks;
        }

        public Boolean getReserve() {
            return reserve;
        }

        public void setReserve(Boolean reserve) {
            this.reserve = reserve;
        }
    }
    public static long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Autowired
    private RentalRepo rentalRepo;
    @Autowired
    private BookRepo bookRepo;

    @Autowired
    private ClientRepo clientRepo;

    @RequestMapping("/")
    public String index(Model model){
        List<Rental> rentals = rentalRepo.findByClientId(1L);
        List<Book> books = bookRepo.findAllById(rentals.stream()
                .map(rental -> rental.getBook().getId())
                .collect(Collectors.toList()));
        rentals.stream()
                .forEach(rental -> {
                    if(getDifferenceDays(rental.getToDate(), new Date())>0) {
                        rental.setPenalty(0.5 * (double) getDifferenceDays(rental.getToDate(), new Date()));
                    }
                });

        List<Book> allBooks = bookRepo.findAll();

        model.addAttribute("rentals", rentals);
        model.addAttribute("books", books);
        model.addAttribute("allBooks", allBooks);
        model.addAttribute("add", new newRental());


        return "index";
    }

    @RequestMapping("/add")
    public String createRental(@ModelAttribute newRental requestRental) throws Exception {
        Book book = bookRepo.findById(requestRental.getBookId()).orElseThrow(Exception::new);
        Client client = clientRepo.findById(1L).orElseThrow(Exception::new);
        Date today = new Date();
        Date toDate = new Date(today.getTime() + (1000L * 60 * 60 * 24 * requestRental.getWeeks() * 7));
        Boolean reservation = requestRental.getReserve();
        Rental r = new Rental(book,client,today,toDate,reservation);
        rentalRepo.save(r);
        return "redirect:/";
    }
}
