package com.paypal.bfs.test.bookingserv.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created By Shivanshu Goyal on 14/08/21 Aug, 2021
 */
@Entity
@Table(name = "idempotency_check")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IdempotencyCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "idempotency_key")
    @JsonProperty("idempotency_key")
    private String idempotencyKey;

    @Column(name = "status")
    @JsonProperty("status")
    private Boolean status;

}
