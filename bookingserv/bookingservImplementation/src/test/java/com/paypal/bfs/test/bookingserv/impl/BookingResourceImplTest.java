package com.paypal.bfs.test.bookingserv.impl;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.paypal.bfs.test.bookingserv.BookingRepository;
import com.paypal.bfs.test.bookingserv.BookingServApplication;
import com.paypal.bfs.test.bookingserv.IdempotencyCheckRepository;
import com.paypal.bfs.test.bookingserv.api.model.Booking;
import com.paypal.bfs.test.bookingserv.util.JsonUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Created By Shivanshu Goyal on 14/08/21 Aug, 2021
 */
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = BookingServApplication.class)
public class BookingResourceImplTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private IdempotencyCheckRepository idempotencyCheckRepository;

    private static final String IDEMPOTENCY_KEY = "Idempotency-Key";

    @Test
    public void testCreateIsSuccessfulForCorrectBookingRequest() throws Exception {

        Booking booking = (Booking) JsonUtil.getObjectFromFile("create_booking.json", Booking.class);

        ResultActions resultActions = mvc.perform(post("/v1/bfs/booking").contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.getJsonStringFromObject(booking)).header(IDEMPOTENCY_KEY, System.currentTimeMillis()));

        Assert.assertEquals(HttpStatus.CREATED.value(), resultActions.andReturn().getResponse().getStatus());

    }

    @Test
    public void testCreateIsFailedWithIdempotencyCheck() throws Exception {

        Booking booking = (Booking) JsonUtil.getObjectFromFile("create_booking.json", Booking.class);

        Long idempotencyKey = System.currentTimeMillis();

        ResultActions resultActions = mvc.perform(post("/v1/bfs/booking").contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.getJsonStringFromObject(booking)).header(IDEMPOTENCY_KEY, idempotencyKey));

        Assert.assertEquals(HttpStatus.CREATED.value(), resultActions.andReturn().getResponse().getStatus());

        resultActions = mvc.perform(post("/v1/bfs/booking").contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.getJsonStringFromObject(booking)).header(IDEMPOTENCY_KEY, idempotencyKey));

        Assert.assertEquals(HttpStatus.CONFLICT.value(), resultActions.andReturn().getResponse().getStatus());

    }

    @Test
    public void testCreateIsFailedWhenFirstNameIsMissing() throws Exception {

        Booking booking = (Booking) JsonUtil.getObjectFromFile("create_booking_fnm.json", Booking.class);

        ResultActions resultActions = mvc.perform(post("/v1/bfs/booking").contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.getJsonStringFromObject(booking)).header(IDEMPOTENCY_KEY, System.currentTimeMillis()));

        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), resultActions.andReturn().getResponse().getStatus());

    }

    @Test(expected = InvalidFormatException.class)
    public void testCreateIsFailedWhenInvalidDateOfBirthIsPassed() throws Exception {

        Booking booking = (Booking) JsonUtil.getObjectFromFile("create_booking_invalid_dob.json", Booking.class);

        mvc.perform(post("/v1/bfs/booking").contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.getJsonStringFromObject(booking)).header(IDEMPOTENCY_KEY, System.currentTimeMillis()));
    }

    @Test
    public void testGetAllReturnsNoContentWhenThereIsNoBookingInDB() throws Exception {

        bookingRepository.deleteAll();

        ResultActions resultActions = mvc.perform(get("/v1/bfs/booking").contentType(MediaType.APPLICATION_JSON));

        Assert.assertEquals(HttpStatus.NO_CONTENT.value(), resultActions.andReturn().getResponse().getStatus());

    }

    @Test
    public void testGetAllReturnBookingWhenPresent() throws Exception {

        bookingRepository.deleteAll();

        Booking booking = (Booking) JsonUtil.getObjectFromFile("create_booking.json", Booking.class);

        ResultActions resultActions = mvc.perform(post("/v1/bfs/booking").contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.getJsonStringFromObject(booking)).header(IDEMPOTENCY_KEY, System.currentTimeMillis()));

        Assert.assertEquals(HttpStatus.CREATED.value(), resultActions.andReturn().getResponse().getStatus());

        resultActions = mvc.perform(get("/v1/bfs/booking").contentType(MediaType.APPLICATION_JSON));

        Assert.assertEquals(HttpStatus.OK.value(), resultActions.andReturn().getResponse().getStatus());
        Assert.assertTrue(resultActions.andReturn().getResponse().getContentAsString().contains("Shivanshu"));
    }

    @After
    public void clearAll() {
        bookingRepository.deleteAll();
        idempotencyCheckRepository.deleteAll();
    }
}
