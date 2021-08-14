package com.paypal.bfs.test.bookingserv;

import com.paypal.bfs.test.bookingserv.entity.Bookings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created By Shivanshu Goyal on 14/08/21 Aug, 2021
 */
@Repository
public interface BookingRepository extends JpaRepository<Bookings, Integer> {

}
