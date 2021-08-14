package com.paypal.bfs.test.bookingserv.impl;

import com.paypal.bfs.test.bookingserv.BookingRepository;
import com.paypal.bfs.test.bookingserv.IdempotencyCheckRepository;
import com.paypal.bfs.test.bookingserv.api.BookingResource;
import com.paypal.bfs.test.bookingserv.api.model.Address;
import com.paypal.bfs.test.bookingserv.api.model.Booking;
import com.paypal.bfs.test.bookingserv.entity.Bookings;
import com.paypal.bfs.test.bookingserv.entity.IdempotencyCheck;
import com.paypal.bfs.test.bookingserv.util.JsonUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class BookingResourceImpl implements BookingResource {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private IdempotencyCheckRepository idempotencyCheckRepository;

    @Autowired
    private HttpServletRequest request;

    private static final String IDEMPOTENCY_KEY = "Idempotency-Key";

    @Override
    @Transactional
    public ResponseEntity<Booking> create(Booking booking) {

        try {
            //validating all mandatory fields in the booking request
            if (!validateCreateRequest(booking)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            //checking if the booking already exists in the database table
            IdempotencyCheck idempotencyCheck = idempotencyCheckRepository.findByIdempotencyKey(request.getHeader(IDEMPOTENCY_KEY));
            if(idempotencyCheck != null && idempotencyCheck.getStatus()) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }

            ResponseEntity<Booking> responseEntity = new ResponseEntity<>(getBookingModel(bookingRepository
                    .save(getBookingEntity(booking))), HttpStatus.CREATED);

            //saving the idempotency key in the database which will help to check request is completed successfully.
            //transactional annotation is added to ensure either all the operations will happen or none of them will be committed.
            idempotencyCheckRepository.save(IdempotencyCheck.builder().idempotencyKey(request.getHeader(IDEMPOTENCY_KEY))
                    .status(true).build());
            return responseEntity;
        } catch (Exception exception) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<List<Booking>> getAll() {

        try {
            List<Bookings> bookingsFromDB = bookingRepository.findAll();

            if (CollectionUtils.isEmpty(bookingsFromDB)) {
                //when there is not booking found in the database
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                //return the list of all bookings
                List<Booking> bookings = new ArrayList<>();
                for(Bookings bookings1: bookingsFromDB) {
                    bookings.add(getBookingModel(bookings1));
                }
                return new ResponseEntity<>(bookings, HttpStatus.OK);
            }
        } catch (Exception exception) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Method to validate the booking request before saving it into the database
     */
    private boolean validateCreateRequest(Booking booking) {

        return StringUtils.isNotBlank(booking.getFirstName()) &&
                StringUtils.isNotBlank(booking.getLastName()) &&
                booking.getDateOfBirth() != null &&
                booking.getCheckinDatetime() != null &&
                booking.getCheckoutDatetime() != null &&
                booking.getDeposit() != null &&
                booking.getTotalPrice() != null &&
                booking.getAddress() != null &&
                StringUtils.isNotBlank(booking.getAddress().getLine1()) &&
                StringUtils.isNotBlank(booking.getAddress().getCity()) &&
                StringUtils.isNotBlank(booking.getAddress().getState()) &&
                StringUtils.isNotBlank(booking.getAddress().getZipCode());
    }

    /**
     * Method to convert the database entity into business model or object
     */
    private Booking getBookingModel(Bookings bookings) throws IOException {

        Booking booking = JsonUtil.getObjectFromAnotherObject(bookings, Booking.class);
        booking.setAddress(JsonUtil.getObjectFromJsonString(Address.class, bookings.getAddressDetails()));
        return booking;
    }

    /**
     * Method to convert the business object into database entity
     */
    private Bookings getBookingEntity(Booking booking) throws IOException {
        Bookings bookings = JsonUtil.getObjectFromAnotherObject(booking, Bookings.class);
        bookings.setAddressDetails(JsonUtil.getJsonStringFromObject(booking.getAddress()));
        return bookings;
    }
}
