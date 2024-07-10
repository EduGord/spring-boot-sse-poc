package com.edugord.server_side_event_poc.repository;

import com.edugord.server_side_event_poc.model.entity.Payment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, UUID>, PagingAndSortingRepository<Payment, UUID> {
}
