package com.paypal.bfs.test.bookingserv;

import com.paypal.bfs.test.bookingserv.entity.IdempotencyCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created By Shivanshu Goyal on 14/08/21 Aug, 2021
 */
@Repository
public interface IdempotencyCheckRepository extends JpaRepository<IdempotencyCheck, Integer> {

    IdempotencyCheck findByIdempotencyKey(final String idempotencyKey);
}
