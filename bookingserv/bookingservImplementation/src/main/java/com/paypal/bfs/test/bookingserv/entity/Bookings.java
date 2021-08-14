package com.paypal.bfs.test.bookingserv.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created By Shivanshu Goyal on 14/08/21 Aug, 2021
 */
@Entity
@Table(name = "bookings")
@Data
public class Bookings {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "first_name")
    @JsonProperty("first_name")
    private String firstName;

    @Column(name = "last_name")
    @JsonProperty("last_name")
    private String lastName;

    @Column(name = "date_of_birth")
    @Temporal(TemporalType.DATE)
    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    @Column(name = "checkin_datetime")
    @Temporal(TemporalType.DATE)
    @JsonProperty("checkin_datetime")
    private Date checkinDateTime;

    @Column(name = "checkout_datetime")
    @Temporal(TemporalType.DATE)
    @JsonProperty("checkout_datetime")
    private Date checkoutDateTime;

    @Column(name = "total_price")
    @JsonProperty("total_price")
    private Double totalPrice;

    @Column(name = "deposit")
    @JsonProperty("deposit")
    private Double deposit;

    @Column(name = "address_details")
    @JsonProperty("address_details")
    private String addressDetails;

}
