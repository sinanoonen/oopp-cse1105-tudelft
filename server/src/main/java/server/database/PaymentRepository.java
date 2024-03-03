package server.database;

import commons.transactions.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for payments.
 */
public interface PaymentRepository extends JpaRepository<Payment, Long> { }
